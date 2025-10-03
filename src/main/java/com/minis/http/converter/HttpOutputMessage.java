package com.minis.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpHeaders;

/**
 * HTTP输出消息接口
 */
public interface HttpOutputMessage {

    /**
     * 获取消息头
     * @return 消息头
     */
    HttpHeaders getHeaders();

    /**
     * 获取消息体输出流
     * @return 输出流
     * @throws IOException 如果获取输出流时发生IO异常
     */
    OutputStream getBody() throws IOException;
}
