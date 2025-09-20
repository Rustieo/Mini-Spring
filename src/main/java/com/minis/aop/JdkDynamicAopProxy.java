package com.minis.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(JdkDynamicAopProxy.class);
    Object target;
    private List<PointcutAdvisor> advisors;
    public JdkDynamicAopProxy(Object target,List<PointcutAdvisor> advisors) {
        this.target = target;
        this.advisors =advisors;
    }
    @Override
    public Object getProxy() {
        Object obj = Proxy.newProxyInstance(JdkDynamicAopProxy.class.getClassLoader(), target.getClass().getInterfaces(), this);
        return obj;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = (target != null ? target.getClass() : null);
        List<Advice> interceptors = getChain(method);
        if (interceptors.isEmpty()) {
            return method.invoke(target, args);
        }
        ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, interceptors);
        return invocation.proceed();
    }
    public List<Advice> getChain(Method method) {
        List<Advice> interceptors = new ArrayList<>();
        if (this.advisors == null || this.advisors.isEmpty()) {
            return interceptors;
        }
        Class<?> targetClass = target.getClass();
        for (PointcutAdvisor advisor : advisors) {
            if (advisor == null) { continue; }
            Pointcut pointcut = advisor.getPointcut();
            if (pointcut == null) { continue; }
            // 先匹配类
            ClassFilter classFilter = pointcut.getClassFilter();
            if (classFilter != null && !classFilter.matches(targetClass)) {
                continue;
            }
            // 再匹配方法
            MethodMatcher methodMatcher = pointcut.getMethodMatcher();
            if (methodMatcher != null && !methodMatcher.matches(method, targetClass)) {
                continue;
            }
            interceptors.add(advisor.getMethodInterceptor());
        }
        return interceptors;
    }
}
