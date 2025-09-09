package com.minis.aop;

public abstract class StaticMethodMatcherPointcut implements Pointcut, MethodMatcher {
    private ClassFilter classFilter ;
    public StaticMethodMatcherPointcut(){
        this.classFilter = ClassFilter.TRUE;
    }
    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }
    //这里声明为final,禁止子类重写
    //如果不禁止,子类在编写getMethodMatcher时,可能会返回不同的MethodMatcher而不是return this
    public final MethodMatcher getMethodMatcher() {
        return this;
    }
}
