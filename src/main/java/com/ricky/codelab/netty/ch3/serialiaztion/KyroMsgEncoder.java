package com.ricky.codelab.netty.ch3.serialiaztion;

import com.ricky.codelab.netty.model.Car;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义Encoder 继承自MessageToByteEncoder<T> 把对象转换成byte
 * @author Ricky
 *
 */
public class KyroMsgEncoder extends MessageToByteEncoder<Car> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Car msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
