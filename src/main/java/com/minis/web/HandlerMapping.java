package com.minis.web;

import jakarta.servlet.http.HttpServletRequest;

public interface HandlerMapping {
    HandlerMethod getHandler(HttpServletRequest request) throws Exception;
}