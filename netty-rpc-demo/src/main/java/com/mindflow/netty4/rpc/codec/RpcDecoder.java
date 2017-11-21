package com.mindflow.netty4.rpc.codec;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Ricky on 2017/5/9.
 */
public class RpcDecoder<T> extends ByteToMessageDecoder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<T> type;

    public RpcDecoder(Class<T> type) {
        this.type = type;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {

        int len = buf.readableBytes();
        final byte[] arr = new byte[len];
        buf.readBytes(arr);

        Schema<T> schema = SchemaCache.getSchema(type);
        T message = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(arr, message, schema);
        list.add(message);
    }
}
