package beans.factory.support;

import beans.AutowiredAnnotationBeanPostProcessor;
import beans.BeanPostProcessor;
import beans.BeansException;
import beans.factory.config.BeanDefinition;

import java.util.ArrayList;
import java.util.List;

public class AutowireCapableBeanFactory extends AbstractBeanFactory{
    private final List<AutowiredAnnotationBeanPostProcessor> beanPostProcessors=new ArrayList<>();
    public void addBeanPostProcessor(AutowiredAnnotationBeanPostProcessor postProcessor){
        if(!beanPostProcessors.contains(postProcessor)){
            beanPostProcessors.add(postProcessor);
        }
    }
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }
    public List<AutowiredAnnotationBeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (AutowiredAnnotationBeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            beanProcessor.setBeanFactory(this);
            result = beanProcessor.postProcessBeforeInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            result = beanProcessor.postProcessAfterInitialization(result,beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }


    @Override
    public void registerBeanDefinition(String name, BeanDefinition bd) {

    }
}
