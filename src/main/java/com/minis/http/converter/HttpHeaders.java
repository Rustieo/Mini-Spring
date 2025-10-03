package com.minis.http.converter;

import com.minis.http.MediaType;

import java.util.*;

/**
 * HTTP头信息类
 */
public class HttpHeaders {
    private final Map<String, List<String>> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>(8);
    }

    /**
     * 获取指定名称的第一个头信息
     * @param headerName 头信息名称
     * @return 头信息值
     */
    public String getFirst(String headerName) {
        List<String> headerValues = this.headers.get(headerName);
        return (headerValues != null && !headerValues.isEmpty() ? headerValues.get(0) : null);
    }

    /**
     * 获取指定名称的所有头信息
     * @param headerName 头信息名称
     * @return 头信息值列表
     */
    public List<String> get(String headerName) {
        return this.headers.get(headerName);
    }

    /**
     * 设置头信息
     * @param headerName 头信息名称
     * @param headerValue 头信息值
     */
    public void set(String headerName, String headerValue) {
        List<String> headerValues = new ArrayList<>(1);
        headerValues.add(headerValue);
        this.headers.put(headerName, headerValues);
    }

    /**
     * 添加头信息
     * @param headerName 头信息名称
     * @param headerValue 头信息值
     */
    public void add(String headerName, String headerValue) {
        List<String> headerValues = this.headers.computeIfAbsent(headerName, k -> new ArrayList<>(1));
        headerValues.add(headerValue);
    }

    /**
     * 设置内容类型
     * @param mediaType 媒体类型
     */
    public void setContentType(MediaType mediaType) {
        if (mediaType != null) {
            set("Content-Type", mediaType.toString());
        }
    }

    /**
     * 获取内容类型
     * @return 媒体类型
     */
    public MediaType getContentType() {
        String value = getFirst("Content-Type");
        return (value != null ? parseMediaType(value) : null);
    }

    /**
     * 解析媒体类型
     * @param mediaTypeValue 媒体类型字符串
     * @return 媒体类型对象
     */
    private MediaType parseMediaType(String mediaTypeValue) {
        String[] parts = mediaTypeValue.split("/");
        if (parts.length == 2) {
            String type = parts[0].trim();
            String subtype = parts[1].trim();
            return new MediaType(type, subtype);
        }
        return null;
    }
}
