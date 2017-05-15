package com.bytebeats.netty4.protocol.netty.codec.kryo;

import com.bytebeats.netty4.common.util.IoUtils;
import com.bytebeats.netty4.protocol.netty.codec.AbstractMsgDecoder;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-16 22:31
 */
public class KryoDecoder extends AbstractMsgDecoder {

    private Kryo kryo = new Kryo();

    public KryoDecoder(Class type) {
        super(type);
    }

    @Override
    protected Object convertToObject(byte[] data) throws IOException {
        Input input = null;
        try {
            input = new Input(new ByteArrayInputStream(data));
            return kryo.readObject(input, this.type);
        } finally {
            IoUtils.closeQuietly(input);
        }
    }

}
