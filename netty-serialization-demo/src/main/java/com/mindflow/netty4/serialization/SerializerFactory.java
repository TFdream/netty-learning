package com.mindflow.netty4.serialization;

import com.mindflow.netty4.serialization.hessian.HessianSerializer;

/**
 * @author Ricky Fung
 */
public class SerializerFactory {

    public static Serializer getSerializer(){
        return new HessianSerializer();
    }
}
