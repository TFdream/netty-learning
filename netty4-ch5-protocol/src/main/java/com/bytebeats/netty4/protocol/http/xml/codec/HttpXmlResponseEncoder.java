package com.bytebeats.netty4.protocol.http.xml.codec;

import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-08 20:35
 */
public class HttpXmlResponseEncoder extends
        AbstractHttpXmlEncoder<HttpXmlResponse> {

    /*
     * (non-Javadoc)
     *
     * @see
     * io.netty.handler.codec.MessageToMessageEncoder#encode(io.netty.channel
     * .ChannelHandlerContext, java.lang.Object, java.util.List)
     */
    protected void encode(ChannelHandlerContext ctx, HttpXmlResponse msg,
                          List<Object> out) throws Exception {
        ByteBuf body = encode0(ctx, msg.getResult());
        FullHttpResponse response = msg.getHttpResponse();
        if (response == null) {
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, body);
        } else {
            response = new DefaultFullHttpResponse(msg.getHttpResponse()
                    .getProtocolVersion(), msg.getHttpResponse().getStatus(),
                    body);
        }
        response.headers().set(CONTENT_TYPE, "text/xml");
        setContentLength(response, body.readableBytes());
        out.add(response);
    }
}
