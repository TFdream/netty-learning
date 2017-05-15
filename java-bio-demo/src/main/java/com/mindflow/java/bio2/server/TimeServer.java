package com.mindflow.java.bio2.server;

import com.mindflow.java.bio.server.TimeServerHandler;
import com.mindflow.java.bio2.TimeServerHandlerExecutor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-03-20 22:49
 */
public class TimeServer {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            TimeServerHandlerExecutor singleExecutor = new TimeServerHandlerExecutor(
                    50, 10000);// 创建IO任务线程池
            while (true) {
                socket = server.accept();
                System.out.println("收到来自客户端的请求");
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                server.close();
            }
        }
    }
}

