package com.mindflow.netty4.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Ricky Fung
 * @date 2016-08-18 14:27
 */
public class PropertiesUtils {

	public static Properties load(File file) throws IOException{
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			Properties props = new Properties();
			props.load(in);
			return props;
		}finally{
			IoUtils.closeQuietly(in);
		}
	}

	public static Properties load(String path) throws IOException{
		InputStream in = null;
		try {
			in = PropertiesUtils.class.getClassLoader().getResourceAsStream(path);
			Properties props = new Properties();
			props.load(in);
			return props;
		}finally{
			IoUtils.closeQuietly(in);
		}
	}

}
