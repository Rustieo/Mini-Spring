package com.minis.http.converter;

/**
 * HTTP消息不可写异常
 */
public class HttpMessageNotWritableException extends RuntimeException {

    public HttpMessageNotWritableException(String message) {
        super(message);
    }

    public HttpMessageNotWritableException(String message, Throwable cause) {
        super(message, cause);
    }
}
