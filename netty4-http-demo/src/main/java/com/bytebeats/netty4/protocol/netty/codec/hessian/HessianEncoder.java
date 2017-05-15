package com.bytebeats.netty4.protocol.netty.codec.hessian;

import com.bytebeats.netty4.protocol.netty.codec.AbstractMsgEncoder;
import com.caucho.hessian.io.Hessian2Output;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-08 22:37
 */
public class HessianEncoder extends AbstractMsgEncoder {

    @Override
    protected byte[] convertToBytes(Object msg) throws IOException {
        Hessian2Output out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new Hessian2Output(bos);
            out.startMessage();

            out.writeObject(msg);
            out.completeMessage();
            out.flush();

            return bos.toByteArray();

        } finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
