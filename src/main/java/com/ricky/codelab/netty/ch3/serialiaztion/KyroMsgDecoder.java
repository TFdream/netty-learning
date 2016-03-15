package com.ricky.codelab.netty.ch3.serialiaztion;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.ricky.codelab.netty.model.Car;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 自定义Decoder 继承ByteToMessageDecoder
 * @author Ricky
 *
 */
public class KyroMsgDecoder extends ByteToMessageDecoder {
	
	public static final int HEAD_LENGTH = 4;

	private Kryo kryo = new Kryo();
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		
		if (in.readableBytes() < HEAD_LENGTH) {  //这个HEAD_LENGTH是我们用于表示头长度的字节数。  由于上面我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
            return;
        }
        in.markReaderIndex();                  //我们标记一下当前的readIndex的位置
        int dataLength = in.readInt();       // 读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
        if (dataLength < 0) { // 我们读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
            ctx.close();
        }
 
        if (in.readableBytes() < dataLength) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
            in.resetReaderIndex();
            return;
        }
 
        byte[] body = new byte[dataLength];  //  嗯，这时候，我们读到的长度，满足我们的要求了，把传送过来的数据，取出来吧~~
        in.readBytes(body);  //
        Object o = convertToObject(body);  //将byte数据转化为我们需要的对象
        out.add(o);
	}
	
	private Object convertToObject(byte[] body) {
		
		Input input = null;
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(body);
			input = new Input(bais);
			
			return kryo.readObject(input, Car.class);
		} catch (KryoException e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(bais);
		}
	    
		return null;
	}

}
