package com.bytebeats.jdk.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-03-20 22:35
 */
public class TimeServer {

    public static void main(String[] args) throws IOException{
        int port = 8080;
        if(args != null && args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){
                //采用默认值
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            while(true) {
                socket = server.accept();    //接收客户端连接请求，没有的时候就阻塞
                System.out.println("收到来自客户端的请求");
                new Thread(new TimeServerHandler(socket)).start();
            }
        } finally{
            if(server != null) {
                System.out.println("The time server close");
                server.close();
            }
        }
    }
}
