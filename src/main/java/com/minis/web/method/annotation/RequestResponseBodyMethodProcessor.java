package com.minis.web.method.annotation;

//TODO 这个类我懒得写了
/*public class RequestResponseBodyMethodProcessor {

    private List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

    *//**
     * 创建RequestResponseBodyMethodProcessor的实例
     *//*
    public RequestResponseBodyMethodProcessor() {
        this(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
    }

    *//**
     * 使用指定的消息转换器创建RequestResponseBodyMethodProcessor实例
     * @param messageConverters 消息转换器列表
     *//*
    public RequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    *//**
     * 使用消息转换器将返回值写入响应
     * @param returnValue 需要转换并写入的返回值对象
     * @param response HTTP响应
     * @throws java.io.IOException 写入过程中可能发生的IO异常
     * @throws com.minis.http.converter.HttpMessageNotWritableException 无法转换消息时抛出的异常
     *//*
    public void handleReturnValue(Object returnValue, HttpServletResponse response)
            throws IOException, HttpMessageNotWritableException {
        // 简化版本，直接处理返回值
        writeValue(returnValue, response);
    }

    *//**
     * 将返回值写入响应
     * @param value 需要转换并写入的返回值对象
     * @param response HTTP响应
     * @throws java.io.IOException IO异常
     * @throws com.minis.http.converter.HttpMessageNotWritableException 消息不可写异常
     *//*
    protected <T> void writeValue(T value, HttpServletResponse response)
            throws IOException, HttpMessageNotWritableException {
        // 设置默认内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON.toString());

        Class<?> valueType;
        if (value != null) {
            valueType = value.getClass();
        } else {
            valueType = Object.class;
            value = (T) "";  // 避免空值导致的转换问题
        }

        // 遍历所有消息转换器，找到能处理当前类型的转换器
        for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
            HttpMessageConverter<T> converter = (HttpMessageConverter<T>) messageConverter;
            if (converter.canWrite(valueType)) {
                converter.write(value, response);
                return;
            }
        }
}*/
