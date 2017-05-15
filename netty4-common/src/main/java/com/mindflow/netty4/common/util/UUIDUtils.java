package com.mindflow.netty4.common.util;

import java.util.UUID;

/**
 *
 * @author Ricky Fung
 * @date 2016-08-18 14:27
 */
public class UUIDUtils {

	public static String getUUID() {

		return UUID.randomUUID().toString().replace("-", "");
	}

}
