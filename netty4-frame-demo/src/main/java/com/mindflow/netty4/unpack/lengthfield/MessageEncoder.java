package com.mindflow.netty4.unpack.lengthfield;

import com.mindflow.netty4.unpack.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.Charset;

/**
 * @author Ricky Fung
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {
    private final Charset charset = Charset.forName("utf-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {

        //
        out.writeByte(msg.getMagicType());
        out.writeByte(msg.getType());
        out.writeLong(msg.getRequestId());

        byte[] data = msg.getBody().getBytes(charset);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
