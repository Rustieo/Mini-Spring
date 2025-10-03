package com.minis.http.converter;

/**
 * HTTP消息不可读异常
 */
public class HttpMessageNotReadableException extends RuntimeException {

    public HttpMessageNotReadableException(String message) {
        super(message);
    }

    public HttpMessageNotReadableException(String message, Throwable cause) {
        super(message, cause);
    }
}
