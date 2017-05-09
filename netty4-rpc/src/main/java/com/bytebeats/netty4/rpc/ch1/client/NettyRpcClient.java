package com.bytebeats.netty4.rpc.ch1.client;

import com.bytebeats.netty4.rpc.ChannelWrapper;
import com.bytebeats.netty4.rpc.InvokeCallback;
import com.bytebeats.netty4.rpc.RpcFuture;
import com.bytebeats.netty4.rpc.codec.RpcDecoder;
import com.bytebeats.netty4.rpc.codec.RpcEncoder;
import com.bytebeats.netty4.rpc.model.Request;
import com.bytebeats.netty4.rpc.model.Response;
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

import java.net.InetSocketAddress;
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

    private String host;
    private int port;

    public NettyRpcClient(String host, int port){
        this.host = host;
        this.port = port;
    }

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
                                new IdleStateHandler(0, 0, 5),
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

    public Response sendSync(final Request request, long timeout, TimeUnit unit) throws Exception {
        ChannelWrapper wrapper = getOrCreateChannelWrapper();

        Channel channel = wrapper.getChannel();
        if (channel != null && channel.isActive()) {

            final RpcFuture<Response> rpcFuture = new RpcFuture<>(timeout, unit);
            this.rpcFutureTable.put(request.getId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        System.out.println("send success");
                        rpcFuture.setSendRequestSuccess(true);
                        return;
                    } else {
                        System.out.println("send failure");
                        rpcFutureTable.remove(request.getId());
                        rpcFuture.setSendRequestSuccess(false);
                        rpcFuture.setFailure(future.cause());
                    }
                }
            });

            return rpcFuture.get(timeout, unit);
        } else {
            throw new IllegalArgumentException("channel not Active...");
        }
    }

    public void sendAsync(final Request request, long timeout, TimeUnit unit, final InvokeCallback callback) throws Exception {
        ChannelWrapper wrapper = getOrCreateChannelWrapper();

        Channel channel = wrapper.getChannel();
        if (channel != null && channel.isActive()) {

            final RpcFuture<Response> rpcFuture = new RpcFuture<>(timeout, unit);
            this.rpcFutureTable.put(request.getId(), rpcFuture);
            //写数据
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        System.out.println("send success");
                        rpcFuture.setSendRequestSuccess(true);
                        return;
                    } else {
                        System.out.println("send failure");
                        rpcFutureTable.remove(request.getId());
                        rpcFuture.setSendRequestSuccess(false);
                        rpcFuture.setFailure(future.cause());
                        //回调
                        callback.onFailure(future.cause());
                    }
                }
            });
        } else {
            throw new RuntimeException("");
        }
    }

    public void sendOneway(Request request, long timeout, TimeUnit unit){

        ChannelWrapper wrapper = getOrCreateChannelWrapper();

        Channel channel = wrapper.getChannel();
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        System.out.println("send success");
                    } else {
                        System.out.println("send failure");
                    }
                }
            });
        }
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<Response> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response msg) throws Exception {

            final Response response = msg;
            log.info("Rpc client receive response id:{}", response.getId());
            RpcFuture future = rpcFutureTable.get(response.getId());
            future.setResult(response);
        }
    }

    private ChannelWrapper getOrCreateChannelWrapper(){
        String address = String.format("%s:%d", host, port);
        ChannelWrapper wrapper = channelTable.get(address);
        if(wrapper==null){
            wrapper = createChannel(host, port);
            channelTable.put(address, wrapper);
        }
        return wrapper;
    }

    private ChannelWrapper createChannel(String host, int port){
        // 发起异步连接操作
        ChannelFuture future = b.connect(
                new InetSocketAddress(host, port));

        return new ChannelWrapper(future);
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
