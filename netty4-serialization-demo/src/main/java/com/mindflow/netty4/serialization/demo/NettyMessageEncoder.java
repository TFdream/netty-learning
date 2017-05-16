package com.mindflow.netty4.serialization.demo;

import com.mindflow.netty4.serialization.Serializer;
import com.mindflow.netty4.serialization.model.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public final class NettyMessageEncoder extends
        MessageToByteEncoder<Request> {

    private Serializer serializer = SerializerFactory.getSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Request msg,
                          ByteBuf sendBuf) throws Exception {

        byte[] data = serializer.encode(msg);

        sendBuf.writeInt(data.length);
        sendBuf.writeBytes(data);
    }
}
