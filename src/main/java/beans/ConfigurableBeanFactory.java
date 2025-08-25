package beans;

import beans.factory.BeanFactory;
import beans.factory.config.SingletonBeanRegistry;

public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    int getBeanPostProcessorCount();


}
