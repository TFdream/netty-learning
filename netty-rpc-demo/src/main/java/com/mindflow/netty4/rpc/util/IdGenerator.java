package com.mindflow.netty4.rpc.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class IdGenerator {
    private static final AtomicInteger generator = new AtomicInteger(0);

    public static Integer getId(){
        return generator.incrementAndGet();
    }
}
