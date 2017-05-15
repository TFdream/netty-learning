package com.mindflow.netty4.protocol.netty.codec.hessian;

import com.mindflow.netty4.protocol.netty.codec.AbstractMsgDecoder;
import com.caucho.hessian.io.Hessian2Input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-08 22:31
 */
public class HessianDecoder extends AbstractMsgDecoder {

    public HessianDecoder(Class type) {
        super(type);
    }

    @Override
    protected Object convertToObject(byte[] data) throws IOException {

        Object result = null;
        ByteArrayInputStream bin = null;
        Hessian2Input in = null;
        try {
            bin = new ByteArrayInputStream(data);
            in = new Hessian2Input(bin);

            in.startMessage();
            result = in.readObject();
            in.completeMessage();
        } finally{
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
