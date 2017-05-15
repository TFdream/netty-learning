package com.bytebeats.netty4.protocol.netty.codec.kryo;

import com.bytebeats.netty4.common.util.IoUtils;
import com.bytebeats.netty4.protocol.netty.codec.AbstractMsgEncoder;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-16 22:25
 */
public class KryoEncoder extends AbstractMsgEncoder {
    private Kryo kryo = new Kryo();

    @Override
    protected byte[] convertToBytes(Object msg) throws IOException {
        // Write Obj to File
        Output output = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            output = new Output(baos);
            kryo.writeObject(output, msg);
            output.flush();

            return baos.toByteArray();
        } finally {
            IoUtils.closeQuietly(output);
            IoUtils.closeQuietly(baos);
        }
    }
}
