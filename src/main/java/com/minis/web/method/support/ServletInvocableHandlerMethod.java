package com.minis.web.method.support;


import com.minis.utils.ClassUtils;
import com.minis.web.method.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@Data
public class ServletInvocableHandlerMethod extends InvocableHandlerMethod {

    private static final Method CALLABLE_METHOD = ClassUtils.getMethod(Callable.class, "call");

    public HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    public ServletInvocableHandlerMethod(Object handler, Method method) {
        super(handler, method);
    }
    public ServletInvocableHandlerMethod(HandlerMethod handlerMethod){
        super(handlerMethod);
    }

    public void invokeAndHandle(HttpServletRequest webRequest, Object... providedArgs) throws Exception {
        Method target = getMethod();
        Class<?>[] paramTypes = target.getParameterTypes();
        Object[] invokeArgs;
        if (paramTypes.length == 0) {
            invokeArgs = new Object[0];
        } else {
            invokeArgs = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> pt = paramTypes[i];
                if (HttpServletRequest.class.isAssignableFrom(pt)) {
                    invokeArgs[i] = webRequest;
                    continue;
                }
                if (providedArgs != null && providedArgs.length > i && providedArgs[i] != null
                        && pt.isAssignableFrom(providedArgs[i].getClass())) {
                    invokeArgs[i] = providedArgs[i];
                } else {
                    invokeArgs[i] = null;
                }
            }
        }
        Object returnValue = invokeForRequest(invokeArgs);

        Object respAttr = webRequest.getAttribute("HTTP_RESPONSE");
        if (respAttr == null) {
            respAttr = webRequest.getAttribute("response");
        }
        HttpServletResponse response = (respAttr instanceof HttpServletResponse) ? (HttpServletResponse) respAttr : null;

        boolean responseBodyPresent = isResponseBodyPresent(target);

        if (responseBodyPresent) {
            if (this.returnValueHandlers != null) {
                this.returnValueHandlers.handleReturnValue(returnValue, webRequest);
            }
            return;
        }

        if (response == null) {
            return;
        }
        if (returnValue == null) {
            return;
        }
        if (returnValue instanceof String) {
            String viewName = (String) returnValue;
            if (viewName.startsWith("redirect:")) {
                String targetLocation = viewName.substring("redirect:".length());
                response.sendRedirect(webRequest.getContextPath() + targetLocation);
            } else {
                webRequest.getRequestDispatcher(viewName).forward(webRequest, response);
            }
        } else {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(String.valueOf(returnValue));
        }
    }

    private boolean isResponseBodyPresent(Method method) {
        if (method.isAnnotationPresent(ResponseBody.class)) return true;
        if (method.getDeclaringClass().isAnnotationPresent(ResponseBody.class)) return true;
        // 兼容 bind 包旧注解（若存在且被加了 RUNTIME）
        try {
            Class<?> legacy = Class.forName("com.minis.web.bind.annotation.ResponseBody");
            if (method.isAnnotationPresent((Class) legacy)) return true;
            if (method.getDeclaringClass().isAnnotationPresent((Class) legacy)) return true;
        } catch (ClassNotFoundException ignored) {}
        return false;
    }
}
