package com.minis.beans.factory.config;

public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);
    Object getSingleton(String beanName);
    boolean containsSingleton(String beanName);
    String[] getSingletonNames();
//    String[] getDependentBeans(String beanName);
//    String[] getDependenciesForBean(String beanName);
//    void registerDependentBean(String beanName, String dependentBeanName);
}