package com.minis.aop;

import com.minis.utils.PatternMatchUtils;

import java.lang.reflect.Method;

public class NameMatchMethodPointcut extends StaticMethodMatcherPointcut implements MethodMatcher, Pointcut{
    private String mappedName = "";
    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }
    @Override
    public boolean matches(Method method, Class<?> targetCLass) {
        if (mappedName.equals(method.getName()) || isMatch(method.getName(), mappedName)) {
            return true;
        }
        return false;
    }
    //核心方法，判断方法名是否匹配给定的模式
    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }
}
