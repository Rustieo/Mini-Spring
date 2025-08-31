package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;

public interface AutowireCapableBeanFactory extends BeanFactory {
    int AUTOWIRE_NO = 0;
    int AUTOWIRE_BY_NAME = 1;
    int AUTOWIRE_BY_TYPE = 2;
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException;
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;

}
