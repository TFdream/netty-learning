package com.mindflow.netty4.rpc.codec;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ricky on 2017/5/8.
 */
public class RpcEncoder<T> extends MessageToByteEncoder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<T> type;

    public RpcEncoder(Class<T> type) {
        this.type = type;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = SchemaCache.getSchema(type);
            byte[] arr = ProtostuffIOUtil.toByteArray((T)msg, schema, buffer);
            out.writeBytes(arr);
        } finally {
            buffer.clear();
        }

    }
}
