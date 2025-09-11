package com.minis.beans.factory.config;

import com.minis.beans.BeansException;



public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {
    default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }

}