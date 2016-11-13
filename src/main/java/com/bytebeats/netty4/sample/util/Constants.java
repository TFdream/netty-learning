package com.bytebeats.netty4.sample.util;

public interface Constants {

	String HOST = System.getProperty("host", "127.0.0.1");
	int PORT = Integer.parseInt(System.getProperty("port", "8007"));
}
