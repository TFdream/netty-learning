package com.bytebeats.netty4.protocol.netty.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-08 21:58
 */
public abstract class AbstractMsgEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    public void encode(Object msg, ByteBuf out) throws Exception {

        int lengthPos = out.writerIndex();
        out.writeBytes(LENGTH_PLACEHOLDER);

        byte[] body = convertToBytes(msg);
        out.writeBytes(body);
        out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
    }

    protected abstract byte[] convertToBytes(Object msg) throws IOException;
}
