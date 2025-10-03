package com.minis.web.method.support;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.http.MediaType;
import com.minis.http.converter.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JacksonReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Autowired
    HttpMessageConverter<?> converter;

    @Override
    public void handleReturnValue(Object returnValue, HttpServletRequest webRequest) throws Exception {
        // 获取 HttpServletResponse（约定放在 request attribute 中）
        Object respAttr = webRequest.getAttribute("HTTP_RESPONSE");
        if (respAttr == null) {
            respAttr = webRequest.getAttribute("response");
        }
        if (!(respAttr instanceof HttpServletResponse)) {
            // 无法取得 response，直接返回（或可抛异常，这里选择静默跳过以保证容错）
            return;
        }
        HttpServletResponse response = (HttpServletResponse) respAttr;

        // 空返回值输出空串
        if (returnValue == null) {
            response.setContentType(MediaType.APPLICATION_JSON.toString());
            response.getWriter().write("");
            return;
        }

        Class<?> valueType = returnValue.getClass();

        // 优先使用注入的 HttpMessageConverter
        if (this.converter != null && safeCanWrite(this.converter, valueType)) {
            writeWithConverter(returnValue, response);
            return;
        }

        // 兜底：简易 JSON 序列化
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        String json = simpleJson(returnValue);
        response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
    }

    private boolean safeCanWrite(HttpMessageConverter<?> c, Class<?> clazz) {
        try {
            return c.canWrite(clazz);
        } catch (Throwable ignore) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void writeWithConverter(Object value, HttpServletResponse response) throws IOException, HttpMessageNotWritableException {
        HttpMessageConverter<Object> conv = (HttpMessageConverter<Object>) this.converter;
        // 适配为 HttpOutputMessage
        HttpOutputMessage outputMessage = new ServletHttpOutputMessage(response);
        conv.write(value, MediaType.APPLICATION_JSON, outputMessage);
        // 若转换器未设置 contentType，则确保设置
        if (!response.isCommitted()) {
            response.setContentType(MediaType.APPLICATION_JSON.toString());
        }
    }

    /**
     * 简单 JSON 序列化（仅处理：字符串、数字、布尔、数组、集合、Map、普通 POJO（递归字段））。
     * 仅用于兜底，未处理循环引用与复杂类型。
     */
    private String simpleJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return quote((String) obj);
        if (obj instanceof Number || obj instanceof Boolean) return String.valueOf(obj);
        if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            List<String> parts = new ArrayList<>(len);
            for (int i = 0; i < len; i++) parts.add(simpleJson(Array.get(obj, i)));
            return '[' + String.join(",", parts) + ']';
        }
        if (obj instanceof Collection) {
            Collection<?> col = (Collection<?>) obj;
            List<String> parts = new ArrayList<>(col.size());
            for (Object o : col) parts.add(simpleJson(o));
            return '[' + String.join(",", parts) + ']';
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            List<String> parts = new ArrayList<>(map.size());
            for (Map.Entry<?, ?> e : map.entrySet()) {
                parts.add(quote(String.valueOf(e.getKey())) + ':' + simpleJson(e.getValue()));
            }
            return '{' + String.join(",", parts) + '}';
        }
        // POJO：反射字段
        List<String> parts = new ArrayList<>();
        for (Field f : getAllFields(obj.getClass())) {
            try {
                f.setAccessible(true);
                Object v = f.get(obj);
                parts.add(quote(f.getName()) + ':' + simpleJson(v));
            } catch (IllegalAccessException ignored) {
            }
        }
        return '{' + String.join(",", parts) + '}';
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null && type != Object.class) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }

    private String quote(String s) {
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + '"';
    }

    /** HttpServletResponse -> HttpOutputMessage 适配器 */
    private static class ServletHttpOutputMessage implements HttpOutputMessage {
        private final HttpServletResponse response;

        ServletHttpOutputMessage(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public HttpHeaders getHeaders() {
            // 使用空 headers 占位
            return HttpHeaders.of(Collections.emptyMap(), (k, v) -> true);
        }

        @Override
        public OutputStream getBody() {
            try {
                return response.getOutputStream();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
