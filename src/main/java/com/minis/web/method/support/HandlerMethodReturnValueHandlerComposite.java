package com.minis.web.method.support;

import com.minis.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 组合多个HandlerMethodReturnValueHandler，用于统一管理和调用返回值处理器
 */
public class HandlerMethodReturnValueHandlerComposite {

    //private final List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
    @Autowired
    private  HandlerMethodReturnValueHandler handler;
    public void handleReturnValue(Object returnValue, HttpServletRequest webRequest) throws Exception {
        handler.handleReturnValue(returnValue,  webRequest);
    }

   /* private HandlerMethodReturnValueHandler selectHandler(Object returnValue, MethodParameter returnType) {
        for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
            if (handler.supportsReturnType(returnType)) {
                return handler;
            }
        }
        return null;
    }*/
}
