package com.mindflow.netty4.rpc.client;

import com.mindflow.netty4.rpc.codec.RpcDecoder;
import com.mindflow.netty4.rpc.codec.RpcEncoder;
import com.mindflow.netty4.rpc.core.ChannelWrapper;
import com.mindflow.netty4.rpc.core.InvokeCallback;
import com.mindflow.netty4.rpc.core.NettyConnHandler;
import com.mindflow.netty4.rpc.core.RpcFuture;
import com.mindflow.netty4.rpc.model.Request;
import com.mindflow.netty4.rpc.model.Response;
import com.mindflow.netty4.rpc.util.NetUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ricky on 2017/5/8.
 */
public class NettyRpcClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap b = new Bootstrap();

    protected final ConcurrentHashMap<String, RpcFuture> rpcFutureTable =
            new ConcurrentHashMap<>(256);

    private final ConcurrentHashMap<String, ChannelWrapper> channelTable =
            new ConcurrentHashMap<>();

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public void start(){
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1<<20, 0, 4, 0, 4),
                                new LengthFieldPrepender(4),
                                new RpcDecoder(Response.class), //
                                new RpcEncoder(Request.class), //
                                new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS),
                                new NettyConnHandler(),
                                new NettyClientHandler());
                    }
                });

        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
            private final AtomicInteger idGenerator = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "Rpc-Scheduled-" + this.idGenerator.incrementAndGet());
            }
        });


        this.scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                scanRpcFutureTable();
            }
        }, 500, 500, TimeUnit.MILLISECONDS);
    }

    public void shutdown(){

        this.scheduledThreadPoolExecutor.shutdown();
        this.group.shutdownGracefully();
    }

    public Response sendSync(String address, final Request request, long timeout, TimeUnit unit) throws Exception {
        Channel channel = getOrCreateChannel(address);

        if (channel != null && channel.isActive()) {

            final RpcFuture<Response> rpcFuture = new RpcFuture<>(timeout, unit);
            this.rpcFutureTable.put(request.getId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        log.info("send success, request id:{}", request.getId());
                        rpcFuture.setSendRequestSuccess(true);
                        return;
                    } else {
                        log.info("send failure, request id:{}", request.getId());
                        rpcFutureTable.remove(request.getId());
                        rpcFuture.setSendRequestSuccess(false);
                        rpcFuture.setFailure(future.cause());
                    }
                }
            });

            return rpcFuture.get(timeout, unit);
        } else {
            throw new IllegalArgumentException("channel not active. request id:"+request.getId());
        }
    }

    public void sendAsync(String address, final Request request, long timeout, TimeUnit unit, final InvokeCallback callback) throws Exception {

        Channel channel = getOrCreateChannel(address);
        if (channel != null && channel.isActive()) {

            final RpcFuture<Response> rpcFuture = new RpcFuture<>(timeout, unit, callback);
            this.rpcFutureTable.put(request.getId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        log.info("send success, request id:{}", request.getId());
                        rpcFuture.setSendRequestSuccess(true);
                        return;
                    } else {
                        log.info("send failure, request id:{}", request.getId());

                        rpcFutureTable.remove(request.getId());
                        rpcFuture.setSendRequestSuccess(false);
                        rpcFuture.setFailure(future.cause());
                        //回调
                        callback.onFailure(future.cause());
                    }
                }
            });
        } else {
            throw new IllegalArgumentException("channel not active. request id:"+request.getId());
        }
    }

    public void sendOneway(String address, final Request request, long timeout, TimeUnit unit){

        Channel channel = getOrCreateChannel(address);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        log.info("send success, request id:{}", request.getId());
                    } else {
                        log.info("send failure, request id:{}", request.getId(), future);
                    }
                }
            });
        } else {
            throw new IllegalArgumentException("channel not active. request id:"+request.getId());
        }
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<Response> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response msg) throws Exception {

            final Response response = msg;
            log.info("Rpc client receive response id:{}", response.getId());
            RpcFuture future = rpcFutureTable.get(response.getId());

            future.setResult(response);
            if(future.isAsync()){   //异步调用
                log.info("Rpc client async callback invoke");
                future.execCallback();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            log.error("捕获异常", cause);
        }
    }

    private Channel getOrCreateChannel(String address){

        ChannelWrapper cw = this.channelTable.get(address);
        if (cw != null && cw.isActive()) {
            return cw.getChannel();
        }

        synchronized (this){
            // 发起异步连接操作
            ChannelFuture channelFuture = b.connect(NetUtils.parseSocketAddress(address));
            cw = new ChannelWrapper(channelFuture);
            this.channelTable.put(address, cw);
        }
        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            long timeout = 5000;
            if (channelFuture.awaitUninterruptibly(timeout)) {
                if (cw.isActive()) {
                    log.info("createChannel: connect remote host[{}] success, {}", address, channelFuture.toString());
                    return cw.getChannel();
                } else {
                    log.warn("createChannel: connect remote host[" + address + "] failed, " + channelFuture.toString(), channelFuture.cause());
                }
            } else {
                log.warn("createChannel: connect remote host[{}] timeout {}ms, {}", address, timeout, channelFuture);
            }
        }
        return null;
    }

    /**定时清理超时Future**/
    private void scanRpcFutureTable() {
        final List<RpcFuture> timeoutReqList = new ArrayList<>();
        Iterator<Map.Entry<String, RpcFuture>> it = this.rpcFutureTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, RpcFuture> next = it.next();
            RpcFuture rep = next.getValue();

            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + 1000) <= System.currentTimeMillis()) {  //超时
                it.remove();
                timeoutReqList.add(rep);
            }
        }

        for (RpcFuture future : timeoutReqList) {
            //释放资源
        }
    }
}
