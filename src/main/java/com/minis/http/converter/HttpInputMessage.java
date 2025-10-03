package com.minis.http.converter;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpHeaders;

/**
 * HTTP输入消息接口
 */
public interface HttpInputMessage {

    /**
     * 获取消息头
     * @return 消息头
     */
    HttpHeaders getHeaders();

    /**
     * 获取消息体输入流
     * @return 输入流
     * @throws IOException 如果获取输入流时发生IO异常
     */
    InputStream getBody() throws IOException;
}
