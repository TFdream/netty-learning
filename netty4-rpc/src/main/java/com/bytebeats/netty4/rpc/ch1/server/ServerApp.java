package com.bytebeats.netty4.rpc.ch1.server;

import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ServerApp {

    public static void main(String[] args) {

        NettyRpcServer server = new NettyRpcServer("127.0.0.1", 9555);

        server.start();

        try {
            TimeUnit.MINUTES.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            server.shutdown();
        }

    }
}
