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
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ricky on 2017/5/8.
 */
public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap b = new Bootstrap();

    protected final ConcurrentHashMap<String, RpcFuture> responseTable =
            new ConcurrentHashMap<>(256);

    private String host = "127.0.0.1";
    private int port = 9555;

    public void start(){
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1<<16, 0, 4, 0, 4),
                                new RpcEncoder(Request.class), //
                                new RpcDecoder(Response.class), //
                                new IdleStateHandler(0, 0, 5),
                                new NettyClientHandler());
                    }
                });

    }

    public void sendOneway(Request request){

        ChannelWrapper wrapper = createChannel(host, port);

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

    public Response sendSync(final Request request) throws Exception {
        ChannelWrapper wrapper = createChannel(host, port);

        Channel channel = wrapper.getChannel();
        if (channel != null && channel.isActive()) {

            final RpcFuture<Response> rpcFuture = new RpcFuture<>();
            this.responseTable.put(request.getId(), rpcFuture);
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
                        responseTable.remove(request.getId());
                        rpcFuture.setSendRequestSuccess(false);
                        rpcFuture.setFailure(future.cause());
                    }
                }
            });

            return rpcFuture.get(5, TimeUnit.SECONDS);
        } else {
            throw new RuntimeException("");
        }
    }

    public void sendAsync(final Request request, final InvokeCallback callback) throws Exception {
        ChannelWrapper wrapper = createChannel(host, port);

        Channel channel = wrapper.getChannel();
        if (channel != null && channel.isActive()) {

            final RpcFuture<Response> rpcFuture = new RpcFuture<>();
            this.responseTable.put(request.getId(), rpcFuture);
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
                        responseTable.remove(request.getId());
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

    private ChannelWrapper createChannel(String host, int port){
        // 发起异步连接操作
        ChannelFuture future = b.connect(
                new InetSocketAddress(host, port));

        return new ChannelWrapper(future);
    }


}
