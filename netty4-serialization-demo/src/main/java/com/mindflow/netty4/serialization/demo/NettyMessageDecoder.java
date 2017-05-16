package com.mindflow.netty4.serialization.demo;

import com.mindflow.netty4.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyMessageDecoder<T> extends LengthFieldBasedFrameDecoder {

    private Serializer serializer = SerializerFactory.getSerializer();
    private Class<T> type;

    public NettyMessageDecoder(Class<T> type, int maxFrameLength, int lengthFieldOffset,
                               int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.type = type;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
            throws Exception {

        ByteBuf buffer = (ByteBuf) super.decode(ctx, in);
        if (buffer == null) {
            return null;
        }
        if (buffer.readableBytes() < 4) {
            throw new Exception("error");
        }

        int length = buffer.readInt();

        byte[] data = new byte[length];
        buffer.readBytes(data);

        return serializer.decode(data, type);
    }
}
