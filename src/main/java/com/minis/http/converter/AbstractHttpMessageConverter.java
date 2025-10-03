package com.minis.http.converter;

import com.minis.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 抽象HTTP消息转换器基类
 */
public abstract class AbstractHttpMessageConverter<T> implements HttpMessageConverter<T> {

    private List<MediaType> supportedMediaTypes = Collections.emptyList();

    public AbstractHttpMessageConverter() {
    }

    public AbstractHttpMessageConverter(MediaType supportedMediaType) {
        this.supportedMediaTypes = Collections.singletonList(supportedMediaType);
    }

    public AbstractHttpMessageConverter(MediaType... supportedMediaTypes) {
        this.supportedMediaTypes = Arrays.asList(supportedMediaTypes);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    /**
     * 设置此转换器支持的媒体类型
     */
    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = new ArrayList<>(supportedMediaTypes);
    }

    @Override
    public final T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return readInternal(clazz, inputMessage);
    }

    @Override
    public final void write(T t, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        writeInternal(t, contentType, outputMessage);
    }

    /**
     * 具体的读取实现，由子类实现
     */
    protected abstract T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException;

    /**
     * 具体的写入实现，由子类实现
     */
    protected abstract void writeInternal(T t, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException;
}
