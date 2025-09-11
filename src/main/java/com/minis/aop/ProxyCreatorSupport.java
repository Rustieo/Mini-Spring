package com.minis.aop;

import java.util.ArrayList;
import java.util.List;

public class ProxyCreatorSupport {
    private Object target;
    private AopProxyFactory aopProxyFactory;
    private List<PointcutAdvisor> advisors = new ArrayList<>();

    public ProxyCreatorSupport() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    protected AopProxy createAopProxy() {
        return getAopProxyFactory().createAopProxy(target, advisors);
    }

    public void addAdvisor(PointcutAdvisor advisor) {
        this.advisors.add(advisor);
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return this.target;
    }

    public AopProxyFactory getAopProxyFactory() {
        return this.aopProxyFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        this.aopProxyFactory = aopProxyFactory;
    }

    public List<PointcutAdvisor> getAdvisors() {
        return this.advisors;
    }
    public List<PointcutAdvisor> setAdvisors(List<PointcutAdvisor> advisors) {
        return this.advisors = advisors;
    }
}
