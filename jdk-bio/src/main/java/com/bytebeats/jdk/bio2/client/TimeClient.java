package com.bytebeats.jdk.bio2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-03-20 22:38
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //采用默认值
            }
        }
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket("127.0.0.1",port);    //创建socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);

            for(int i=0; i<1000; i++){
                out.println("QUERY TIME ORDER"); //向服务端写出QUERY TIME ORDER
                System.out.println("Send order 2 server succeed.");
            }
            String resp = in.readLine(); //从服务端读入
            System.out.println("Now is : " +  resp);
        } catch (Exception e) {
            //不需要处理
        } finally {
            if(out != null ){
                out.close();
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}