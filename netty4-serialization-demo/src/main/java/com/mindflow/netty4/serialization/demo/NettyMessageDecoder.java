package com.mindflow.netty4.serialization.demo;

import com.mindflow.netty4.serialization.Serializer;
import com.mindflow.netty4.serialization.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private Serializer serializer = SerializerFactory.getSerializer();

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset,
                               int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer)
            throws Exception {

        if(buffer==null){
            return null;
        }
        if (buffer.readableBytes() < 4) {
            throw new Exception("error");
        }

        int length = buffer.readInt();

        byte[] data = new byte[length];
        buffer.readBytes(data);

        return serializer.decode(data, Message.class);
    }
}
