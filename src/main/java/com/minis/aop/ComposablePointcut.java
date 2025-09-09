package com.minis.aop;

import java.lang.reflect.Method;
//TODO 未完成也懒得完成
public class ComposablePointcut implements Pointcut,MethodMatcher{
    ClassFilter classFilter;
    MethodMatcher methodMatcher;
    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    @Override
    public ClassFilter getClassFilter() {
        return classFilter;
    }

    @Override
    public boolean matches(Method method, Class<?> targetCLass) {
        if(classFilter.matches(targetCLass)){
            return methodMatcher.matches(method,targetCLass);
        }
        return false;
    }
}
