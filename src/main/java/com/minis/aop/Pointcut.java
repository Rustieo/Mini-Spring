package com.minis.aop;

public interface Pointcut {
    MethodMatcher getMethodMatcher();
    ClassFilter getClassFilter();
}
