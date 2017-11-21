package com.mindflow.netty4.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindflow.netty4.serialization.Serializer;
import java.io.IOException;

/**
 * @author Ricky Fung
 */
public class JacksonSerializer implements Serializer {

    private static final String CHARSET = "UTF-8";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] encode(Object msg) throws IOException {
        String jsonString = objectMapper.writeValueAsString(msg);
        return jsonString.getBytes(CHARSET);
    }

    @Override
    public <T> T decode(byte[] buf, Class<T> type) throws IOException {
        String jsonString =  new String(buf, CHARSET);
        return objectMapper.readValue(jsonString, type);
    }
}