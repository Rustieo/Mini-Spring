package com.minis.http.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minis.http.MediaType;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 基于Jackson 2.x的HTTP消息转换器，用于处理JSON格式的请求和响应
 * 根据返回类型的不同，采取不同的处理策略：
 * 1. 如果是对象，则使用Jackson将其转换为JSON
 * 2. 如果是基本数据类型或String，则直接输出
 */
public class MappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    private String jsonPrefix;

    /**
     * 默认构造函数，使用默认配置的ObjectMapper
     */
    public MappingJackson2HttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.json().build());
    }

    /**
     * 使用指定的ObjectMapper构造转换器
     * @param objectMapper 自定义的ObjectMapper
     */
    public MappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    /**
     * 设置JSON前缀
     * @param jsonPrefix 自定义前缀
     * @see #setPrefixJson
     */
    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    /**
     * 设置是否添加防止JSON劫持的前缀
     * @param prefixJson 是否添加前缀
     * @see #setJsonPrefix
     */
    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? ")]}', " : null);
    }

    /**
     * 在写入JSON数据前添加前缀
     */
    @Override
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
    }

    /**
     * 覆盖父类的写入方法，实现基本数据类型和String直接输出的功能
     */
    @Override
    protected void writeInternal(Object object, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        // 如果是基本数据类型或String，直接输出
        if (isPrimitiveOrString(object)) {
            Writer writer = new OutputStreamWriter(outputMessage.getBody(), "UTF-8");
            writer.write(object.toString());
            writer.flush();
        } else {
            // 对象类型，使用Jackson转换为JSON
            super.writeInternal(object, contentType, outputMessage);
        }
    }

    /**
     * 判断对象是否为基本数据类型或String
     */
    private boolean isPrimitiveOrString(Object obj) {
        if (obj == null) {
            return false;
        }

        Class<?> clazz = obj.getClass();

        // 检查基本数据类型及其包装类
        return clazz.isPrimitive() ||
               clazz == String.class ||
               clazz == Boolean.class ||
               clazz == Character.class ||
               clazz == Byte.class ||
               clazz == Short.class ||
               clazz == Integer.class ||
               clazz == Long.class ||
               clazz == Float.class ||
               clazz == Double.class;
    }

    /**
     * 判断是否可以处理特定类型
     */
    @Override
    public boolean canWrite(Class<?> clazz) {
        // 基本数据类型、String或对象都可以处理
        return isPrimitiveOrString(clazz.getComponentType() != null ? clazz.getComponentType() : clazz) ||
               super.canWrite(clazz);
    }
}

