package com.minis.aop;

public class ProxyFactory extends ProxyCreatorSupport {
    public ProxyFactory(Object target) {
        setTarget(target);
    }

    public Object getProxy() {
        AopProxy aopProxy = createAopProxy();
        return aopProxy.getProxy();
    }
}
