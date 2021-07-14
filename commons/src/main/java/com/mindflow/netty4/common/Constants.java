package com.mindflow.netty4.common;

import java.nio.charset.Charset;

/**
 *
 * @author Ricky Fung
 */
public final class Constants {

	public static final String HOST = System.getProperty("host", "127.0.0.1");
	public static final int PORT = Integer.parseInt(System.getProperty("port", "9001"));

	public static final String DELIMITER = "$#";

	public final static String CHARSET_NAME = "UTF-8";

	public final static Charset UTF_8 = Charset.forName(CHARSET_NAME);
}
