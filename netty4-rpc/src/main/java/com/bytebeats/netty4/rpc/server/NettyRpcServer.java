package com.bytebeats.netty4.rpc.server;

import com.bytebeats.netty4.rpc.codec.RpcDecoder;
import com.bytebeats.netty4.rpc.codec.RpcEncoder;
import com.bytebeats.netty4.rpc.core.NettyConnHandler;
import com.bytebeats.netty4.rpc.model.Request;
import com.bytebeats.netty4.rpc.model.Response;
import com.bytebeats.netty4.rpc.model.RpcType;
import com.bytebeats.netty4.rpc.util.NetUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ricky on 2017/5/9.
 */
public class NettyRpcServer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    private ThreadPoolExecutor pool;    //业务处理线程池

    public void start(final String address){
        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128) //
                .option(ChannelOption.SO_KEEPALIVE, true) //
                .childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws IOException {

                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1<<20, 0, 4, 0, 4),
                                new LengthFieldPrepender(4),
                                new RpcDecoder(Request.class), //
                                new RpcEncoder(Response.class), //
                                new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS),
                                new NettyConnHandler(),
                                new NettyServerHandler());
                    }
                });

        pool = new ThreadPoolExecutor(50, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            private final AtomicInteger idGenerator = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "NettyServer-" + this.idGenerator.incrementAndGet());
            }
        });

        try {
            ChannelFuture channelFuture = this.serverBootstrap.bind(NetUtils.parseSocketAddress(address)).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {

                    if(f.isSuccess()){
                        log.info("Rpc Server start at address:{} success", address);
                    } else {
                        log.error("Rpc Server start at address:{} failure", address);
                    }
                }
            });
            System.out.println("Rpc Server start..."+address);

        } catch (InterruptedException e) {
            throw new RuntimeException("bind server error", e);
        }
    }

    public void shutdown(){

        this.pool.shutdown();
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    class NettyServerHandler extends SimpleChannelInboundHandler<Request> {

        @Override
        protected void channelRead0(ChannelHandlerContext context, Request request) throws Exception {

            log.info("Rpc server receive request id:{}", request.getId());
            //处理请求
            processRpcRequest(context, request);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            log.error("捕获异常", cause);
        }
    }

    private void processRpcRequest(final ChannelHandlerContext context, final Request request) {

        this.pool.execute(new Runnable() {
            @Override
            public void run() {

                int time = new Random().nextInt(500);
                log.info("Rpc server process request:{}, time:{} start...", request.getId(), time);
                try {
                    TimeUnit.MILLISECONDS.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Response response = new Response();
                response.setId(request.getId());
                response.setResult("Rpc Result:"+time);

                if(request.getType()!= RpcType.ONE_WAY){    //非单向调用
                    context.writeAndFlush(response);
                }
                log.info("Rpc server process request:{}, time:{} end...", request.getId(), time);
            }
        });
    }
}
