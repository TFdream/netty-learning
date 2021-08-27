package com.mindflow.netty4.common;

/**
 *
 * @author Ricky Fung
 */
public final class Constants {

	public static final String HOST = System.getProperty("host", "127.0.0.1");
	public static final int PORT = Integer.parseInt(System.getProperty("port", "9001"));

	public static final String DELIMITER = "$#";

}
