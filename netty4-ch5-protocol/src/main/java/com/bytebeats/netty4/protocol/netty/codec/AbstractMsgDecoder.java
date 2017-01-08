package com.bytebeats.netty4.protocol.netty.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-08 21:57
 */
public abstract class AbstractMsgDecoder {
    private static final int HEAD_LENGTH = 4;

    public Object decode(ByteBuf in) throws Exception {
        if (in.readableBytes() < HEAD_LENGTH) {
            System.out.println("head length < 4");
            return null;
        }
        int objectSize = in.readInt();
        if (objectSize < 0) {
            throw new IllegalArgumentException("objectSize is zero");
        }

        if (in.readableBytes() < objectSize) {
            System.out.println("readableBytes < objectSize");
            in.resetReaderIndex();
            return null;
        }

        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        byte[] body = new byte[objectSize];
        buf.readBytes(body);
        Object obj = convertToObject(body);
        in.readerIndex(in.readerIndex() + objectSize);
        return obj;
    }

    protected abstract Object convertToObject(byte[] data) throws IOException;
}
