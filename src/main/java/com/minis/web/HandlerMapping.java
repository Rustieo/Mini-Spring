package com.minis.web;

import com.minis.web.method.support.HandlerMethod;
import jakarta.servlet.http.HttpServletRequest;

public interface HandlerMapping {
    HandlerMethod getHandler(HttpServletRequest request) throws Exception;
}