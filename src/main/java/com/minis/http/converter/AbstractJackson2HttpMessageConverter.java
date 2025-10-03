package com.minis.http.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minis.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 基于Jackson 2.x的抽象HTTP消息转换器
 */
public abstract class AbstractJackson2HttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    protected ObjectMapper objectMapper;

    /**
     * 构造函数
     * @param objectMapper Jackson对象映射器
     * @param supportedMediaTypes 支持的媒体类型
     */
    protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
        this.objectMapper = objectMapper;
    }

    /**
     * 设置ObjectMapper
     * @param objectMapper Jackson对象映射器
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 获取ObjectMapper
     * @return Jackson对象映射器
     */
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    public boolean canRead(Class<?> clazz) {
        return this.objectMapper.canDeserialize(this.objectMapper.constructType(clazz));
    }

    @Override
    public boolean canWrite(Class<?> clazz) {
        return this.objectMapper.canSerialize(clazz);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        try {
            return this.objectMapper.readValue(inputMessage.getBody(), clazz);
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("JSON解析错误: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void writeInternal(Object object, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        try {
            OutputStream out = outputMessage.getBody();
            JsonGenerator generator = this.objectMapper.getFactory().createGenerator(out);

            // 允许子类在写入数据之前进行前缀处理
            writePrefix(generator, object);

            this.objectMapper.writeValue(generator, object);

            // 允许子类在写入数据之后进行后缀处理
            writeSuffix(generator, object);

            generator.flush();
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("JSON写入错误: " + ex.getMessage(), ex);
        }
    }

    /**
     * 在写入JSON之前调用，可以添加前缀内容
     * @param generator JSON生成器
     * @param object 要序列化的对象
     * @throws IOException IO异常
     */
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        // 由子类实现
    }

    /**
     * 在写入JSON之后调用，可以添加后缀内容
     * @param generator JSON生成器
     * @param object 已序列化的对象
     * @throws IOException IO异常
     */
    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
        // 由子类实现
    }
}
