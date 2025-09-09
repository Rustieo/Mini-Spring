package com.minis.aop;

import java.util.List;

public class DefaultAopProxyFactory implements AopProxyFactory{
    @Override
    public AopProxy createAopProxy(Object target, List<PointcutAdvisor> advisors) {
        return new JdkDynamicAopProxy(target, advisors);
    }
}
