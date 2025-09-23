package com.minis.aop;

import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation implements MethodInvocation{
    protected final Object proxy;
    protected final Object target;
    protected final Method method;
    protected Object[] arguments;
    private Class<?> targetClass;
    private List<Advice> interceptors;
    private int currentInterceptorIndex = 0;

    protected ReflectiveMethodInvocation(
            Object proxy,  Object target, Method method,  Object[] arguments,
            Class<?> targetClass, List<Advice> interceptors) {

        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptors = interceptors;
    }

    public final Object getProxy() {
        return this.proxy;
    }

    public final Object getThis() {
        return this.target;
    }

    public final Method getMethod() {
        return this.method;
    }

    public final Object[] getArguments() {
        return this.arguments;
    }

    public void setArguments(Object... arguments) {
        this.arguments = arguments;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Object proceed() throws Throwable {
        //XXX Spring源码是size-1
        if (this.currentInterceptorIndex == this.interceptors.size()) {
            //调用原来的方法
            return invokeJoinpoint();
        }
        MethodInterceptor interceptor = (MethodInterceptor) this.interceptors.get(this.currentInterceptorIndex++);
        return interceptor.invoke(this);
    }

    private Object invokeJoinpoint() throws Throwable {
        return this.method.invoke(this.target, this.arguments);
    }
}