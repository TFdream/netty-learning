package com.mindflow.netty4.serialization.demo;

import com.mindflow.netty4.serialization.Serializer;
import com.mindflow.netty4.serialization.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public final class NettyMessageEncoder extends
        MessageToByteEncoder<Message> {

    private Serializer serializer = SerializerFactory.getSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg,
                          ByteBuf sendBuf) throws Exception {

        byte[] data = serializer.encode(msg);

        sendBuf.writeInt(data.length);
        sendBuf.writeBytes(data);
    }
}
