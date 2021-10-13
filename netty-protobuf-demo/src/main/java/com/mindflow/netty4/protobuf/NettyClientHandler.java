package com.mindflow.netty4.protobuf;

import com.mindflow.netty4.protobuf.model.UserModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ricky Fung
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    /**
     * 通道就绪触发该方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("channelActive");
        // 发送 User POJO 对象到服务器
        UserModel.User user = UserModel.User.newBuilder().setId(10).setName("客户端张三").build();
        ctx.writeAndFlush(user);
    }
    
    /**
     * 当通道有读取事件时触发该方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取服务器发送的数据 UserMOdel.User
        UserModel.User user = (UserModel.User) msg;
        LOG.info("收到服务器响应 id={}, name={}", user.getId(), user.getName());
    }
}
