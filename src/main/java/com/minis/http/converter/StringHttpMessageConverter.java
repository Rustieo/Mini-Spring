package com.minis.http.converter;

import com.minis.http.MediaType;

import java.io.IOException;

public class StringHttpMessageConverter extends AbstractHttpMessageConverter{
    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(Object object, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

    }

    @Override
    public boolean canRead(Class clazz) {
        return false;
    }

    @Override
    public boolean canWrite(Class clazz) {
        return false;
    }
}
