package com.ricky.codelab.netty.ch3.serialiaztion;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;
import com.ricky.codelab.netty.model.Car;

/**
 * 自定义Encoder
 * @author Ricky
 *
 */
public class KyroMsgEncoder extends MessageToByteEncoder<Car> {

	private Kryo kryo = new Kryo();
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Car msg, ByteBuf out) throws Exception {
		
		byte[] body = convertToBytes(msg);  //将对象转换为byte
        int dataLength = body.length;  //读取消息的长度
        out.writeInt(dataLength);  //先将消息长度写入，也就是消息头
        out.writeBytes(body);  //消息体中包含我们要发送的数据
	}

	private byte[] convertToBytes(Car car) {
		
		ByteArrayOutputStream bos = null;
		Output output = null;
		try {
			bos = new ByteArrayOutputStream();
			output = new Output(bos);
			kryo.writeObject(output, car);
			output.flush();
			
			return bos.toByteArray();
		} catch (KryoException e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(bos);
		}
		return null;
	}

}
