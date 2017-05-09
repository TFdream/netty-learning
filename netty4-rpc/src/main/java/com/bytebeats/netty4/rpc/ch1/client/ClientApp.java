package com.bytebeats.netty4.rpc.ch1.client;

import com.bytebeats.netty4.rpc.model.Request;
import com.bytebeats.netty4.rpc.model.Response;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class ClientApp {

    public static void main(String[] args) {

        NettyRpcClient client = new NettyRpcClient("127.0.0.1", 9555);

        client.start();

        for(int i=0; i<1000; i++){

            Request request = new Request();
            request.setId(UUID.randomUUID().toString());
            try {
                Response response = client.sendSync(request, 1000, TimeUnit.MILLISECONDS);
                System.out.println("send request:"+request.getId()+", receive response id:"+response.getId()+",result:"+response.getResult());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        client.shutdown();
    }
}
