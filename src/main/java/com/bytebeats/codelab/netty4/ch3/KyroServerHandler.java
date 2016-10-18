package com.bytebeats.codelab.netty4.ch3;

import com.bytebeats.codelab.netty4.model.Car;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class KyroServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	
        System.out.println("server receive msg:"+msg);
        
        Car car = new Car();
        car.setName("K5");
        car.setBrand("KIA");
        car.setPrice(24.5);
        car.setSpeed(196);
		
		System.out.println("server write msg:"+car);
        ctx.writeAndFlush(car);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}