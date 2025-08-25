package minis.beans.factory.config;

import minis.beans.BeansException;
import minis.beans.factory.BeanFactory;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

    void setBeanFactory(BeanFactory beanFactory);
}

