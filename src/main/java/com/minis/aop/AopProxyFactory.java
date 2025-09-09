package com.minis.aop;

import java.util.List;

public interface AopProxyFactory {
    AopProxy createAopProxy(Object target, List<PointcutAdvisor> advisors);
}
