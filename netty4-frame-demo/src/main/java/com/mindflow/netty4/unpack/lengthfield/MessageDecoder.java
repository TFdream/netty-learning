package com.mindflow.netty4.unpack.lengthfield;

import com.mindflow.netty4.unpack.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ricky Fung
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    //头部信息的大小应该是 byte+byte+int = 1+1+8+4 = 14
    private static final int HEADER_SIZE = 14;

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null) {
            return null;
        }

        if (in.readableBytes() <= HEADER_SIZE) {
            return null;
        }

        in.markReaderIndex();

        byte magic = in.readByte();
        byte type = in.readByte();
        long requestId = in.readLong();
        int dataLength = in.readInt();

        // FIXME 如果dataLength过大，可能导致问题
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return null;
        }

        byte[] data = new byte[dataLength];
        in.readBytes(data);

        String body = new String(data, "UTF-8");
        Message msg = new Message(magic, type, requestId, body);
        return msg;
    }
}
