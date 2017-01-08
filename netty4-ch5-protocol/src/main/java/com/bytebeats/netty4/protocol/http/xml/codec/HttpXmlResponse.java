package com.bytebeats.netty4.protocol.http.xml.codec;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-08 20:19
 */
public class HttpXmlResponse {
    private FullHttpResponse httpResponse;
    private Object result;

    public HttpXmlResponse(FullHttpResponse httpResponse, Object result) {
        this.httpResponse = httpResponse;
        this.result = result;
    }

    /**
     * @return the httpResponse
     */
    public final FullHttpResponse getHttpResponse() {
        return httpResponse;
    }

    /**
     * @param httpResponse
     *            the httpResponse to set
     */
    public final void setHttpResponse(FullHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    /**
     * @return the body
     */
    public final Object getResult() {
        return result;
    }

    /**
     * @param result
     *            the body to set
     */
    public final void setResult(Object result) {
        this.result = result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HttpXmlResponse [httpResponse=" + httpResponse + ", result="
                + result + "]";
    }

}
