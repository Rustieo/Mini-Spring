package com.minis.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
    void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
}