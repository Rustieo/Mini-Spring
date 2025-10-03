package com.minis.http.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Jackson2 ObjectMapper构建器
 */
public class Jackson2ObjectMapperBuilder {
    private boolean createXmlMapper = false;

    private boolean failOnUnknownProperties = false;

    private boolean failOnEmptyBeans = false;

    private boolean indentOutput = false;

    /**
     * 创建XML映射器
     */
    public Jackson2ObjectMapperBuilder createXmlMapper(boolean createXmlMapper) {
        this.createXmlMapper = createXmlMapper;
        return this;
    }

    /**
     * 设置遇到未知属性时是否失败
     */
    public Jackson2ObjectMapperBuilder failOnUnknownProperties(boolean failOnUnknownProperties) {
        this.failOnUnknownProperties = failOnUnknownProperties;
        return this;
    }

    /**
     * 设置遇到空JavaBean时是否失败
     */
    public Jackson2ObjectMapperBuilder failOnEmptyBeans(boolean failOnEmptyBeans) {
        this.failOnEmptyBeans = failOnEmptyBeans;
        return this;
    }

    /**
     * 设置是否缩进输出
     */
    public Jackson2ObjectMapperBuilder indentOutput(boolean indentOutput) {
        this.indentOutput = indentOutput;
        return this;
    }

    /**
     * 创建JSON ObjectMapper
     */
    public static Jackson2ObjectMapperBuilder json() {
        return new Jackson2ObjectMapperBuilder();
    }

    /**
     * 创建XML ObjectMapper
     */
    public static Jackson2ObjectMapperBuilder xml() {
        return new Jackson2ObjectMapperBuilder().createXmlMapper(true);
    }

    /**
     * 构建ObjectMapper
     */
    public ObjectMapper build() {
        ObjectMapper objectMapper;
        if (this.createXmlMapper) {
            // XML处理逻辑，简化版直接创建普通ObjectMapper
            objectMapper = new ObjectMapper();
        } else {
            objectMapper = new ObjectMapper();
        }

        // 配置序列化特性
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, this.failOnEmptyBeans);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.indentOutput);

        // 配置反序列化特性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, this.failOnUnknownProperties);

        return objectMapper;
    }
}
