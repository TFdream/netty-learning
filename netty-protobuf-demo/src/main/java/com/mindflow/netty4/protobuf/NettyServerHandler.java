package com.mindflow.netty4.protobuf;

import com.mindflow.netty4.protobuf.model.UserModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ricky Fung
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("channelActive");
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取客户端发送的数据 UserMOdel.User
        UserModel.User user = (UserModel.User) msg;
        LOG.info("服务端收到请求数据 id={}, name={}", user.getId(), user.getName());
        
        //
        UserModel.User response = UserModel.User.newBuilder().setId(24).setName("服务器响应").build();
        ctx.writeAndFlush(response);
    }
    
}
