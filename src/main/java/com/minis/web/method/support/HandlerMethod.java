package com.minis.web.method.support;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
@Data
@NoArgsConstructor
public class HandlerMethod {
    private Object bean;
    private Class<?> beanType;
    private Method method;
    private Class<?> returnType;
    private String description;
    private String className;
    private String methodName;

    public HandlerMethod(Method method, Object obj) {
        this.setMethod(method);
        this.setBean(obj);
    }
    public HandlerMethod(HandlerMethod handlerMethod){
        this.method=handlerMethod.getMethod();
        this.bean=handlerMethod.getBean();
        this.beanType=handlerMethod.getBeanType();
        this.returnType=handlerMethod.getReturnType();
        this.description=handlerMethod.getDescription();
        this.className=handlerMethod.getClassName();
        this.methodName=handlerMethod.getMethodName();
    }
    public Method getMethod() {
        return method;
    }
    //NOTE 这里并未实现bridgedMethod
    protected Method getBridgedMethod() {
        return this.method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }
    public Object getBean() {
        return bean;
    }
    public void setBean(Object bean) {
        this.bean = bean;
    }

}