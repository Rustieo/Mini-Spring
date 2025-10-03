package com.minis.web.method.support;

import jakarta.servlet.http.HttpServletRequest;

public interface HandlerMethodReturnValueHandler {



    //boolean supportsReturnType(MethodParameter returnType);
    // 2,5,3,7,45,1,6

    void handleReturnValue(Object returnValue, HttpServletRequest webRequest) throws Exception;

}