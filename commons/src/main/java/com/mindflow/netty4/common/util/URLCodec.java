package com.mindflow.netty4.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Ricky Fung
 */
public class URLCodec {
    private static final String CHARSET = "UTF-8";

    public static String encode(String data) {
        try {
            return URLEncoder.encode(data, CHARSET);
        } catch (UnsupportedEncodingException e) {
            //ignore
            throw new RuntimeException(e);
        }
    }

    public static String decode(String data) {
        try {
            return URLDecoder.decode(data, CHARSET);
        } catch (UnsupportedEncodingException e) {
            //ignore
            throw new RuntimeException(e);
        }
    }
}
