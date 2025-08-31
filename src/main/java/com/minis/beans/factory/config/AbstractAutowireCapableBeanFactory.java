package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.beans.BeansException;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    public AbstractAutowireCapableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
    }

    public AbstractAutowireCapableBeanFactory() {
        super();
    }

    //TODO 这个方法的逻辑有点混乱,好多类都各自实现了
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        if(!beanPostProcessors.contains(beanPostProcessor)){
            beanPostProcessors.add(beanPostProcessor);
        }
    }
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            beanProcessor.setBeanFactory(this);
            result = beanProcessor.postProcessBeforeInitialization(result,
                    beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }
    public Object applyBeanPostProcessorsAfterInitialization(Object
                                                                     existingBean, String beanName)
            throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            result = beanProcessor.postProcessAfterInitialization(result,
                    beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }
}
