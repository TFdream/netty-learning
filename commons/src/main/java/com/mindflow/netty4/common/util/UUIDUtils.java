package com.mindflow.netty4.common.util;

import java.util.UUID;

/**
 *
 * @author Ricky Fung
 */
public class UUIDUtils {

	public static String getUUID() {

		return UUID.randomUUID().toString().replace("-", "");
	}

}
