package com.minis.web;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.method.support.HandlerMethod;
import com.minis.web.method.support.HandlerMethodReturnValueHandlerComposite;
import com.minis.web.method.support.ServletInvocableHandlerMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    @Autowired
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        try {
            invokeHandlerMethod(request, response, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //1 3 2 4 5
    protected void invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ServletInvocableHandlerMethod invocableMethod=new ServletInvocableHandlerMethod(handlerMethod);
        if(invocableMethod.getReturnValueHandlers()==null){
            invocableMethod.setReturnValueHandlers(this.returnValueHandlers);
        }
        // 确保下游能够取得 HttpServletResponse
        if (request.getAttribute("HTTP_RESPONSE") == null) {
            request.setAttribute("HTTP_RESPONSE", response);
        }
        //TODO 不知道为什么源代码这里也没传入参数,改天看下
        invocableMethod.invokeAndHandle(request);
    }



}