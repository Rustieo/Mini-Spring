package com.minis.http.converter;

import com.minis.http.MediaType;

import java.io.IOException;
import java.util.List;

/**
 * HTTP消息转换器接口
 */
public interface HttpMessageConverter<T> {

    /**
     * 判断此转换器是否能读取指定类型
     * @param clazz 目标类型
     * @return 是否支持读取
     */
    boolean canRead(Class<?> clazz);

    /**
     * 判断此转换器是否能写入指定类型
     * @param clazz 目标类型
     * @return 是否支持写入
     */
    boolean canWrite(Class<?> clazz);

    /**
     * 获取支持的媒体类型
     * @return 媒体类型列表
     */
    List<MediaType> getSupportedMediaTypes();

    /**
     * 读取HTTP请求体并转换为对象
     * @param clazz 目标类型
     * @param inputMessage HTTP输入消息
     * @return 转换后的对象
     * @throws IOException 读取异常
     * @throws HttpMessageNotReadableException 消息不可读异常
     */
    T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException;

    /**
     * 将对象转换为HTTP响应体
     * @param t 要转换的对象
     * @param contentType 内容类型
     * @param outputMessage HTTP输出消息
     * @throws IOException 写入异常
     * @throws HttpMessageNotWritableException 消息不可写异常
     */
    void write(T t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException;
}
