package com.minis.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AdvisorSupport {
    private List<Advisor> advisors;
    public AdvisorSupport(List<Advisor> advisors) {
        this.advisors = advisors;
    }
    public List<Advisor> getAdvisors() {
        return advisors;
    }
    public void setAdvisors(List<Advisor> advisors) {
        this.advisors = advisors;
    }
    public List<Advice> getInterceptors() {
        List<Advice> interceptors = new ArrayList<>();
        for (Advisor advisor : advisors) {
            interceptors.add(advisor.getMethodInterceptor());
        }
        return interceptors;
    }
    public List<Advice> getChain(Method method) {
        List<Advice> interceptors = new ArrayList<>();
        for (Advisor advisor : advisors) {
            interceptors.add(advisor.getMethodInterceptor());
        }
        return interceptors;
    }

}
