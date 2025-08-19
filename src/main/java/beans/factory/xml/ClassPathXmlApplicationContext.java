package beans.factory.xml;

import beans.AutowiredAnnotationBeanPostProcessor;
import beans.BeansException;
import beans.Resource;
import beans.factory.ApplicationEvent;
import beans.factory.config.BeanDefinition;
import beans.factory.support.AutowireCapableBeanFactory;

import java.util.List;

public class ClassPathXmlApplicationContext {
    AutowireCapableBeanFactory beanFactory;
    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }
    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        Resource resource = new ClassPathXmlResource(fileName);
        beanFactory=new AutowireCapableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        List<BeanDefinition>nonLazyInitBeans=reader.loadBeanDefinitions(resource);

        if (isRefresh) {
            try {
                this.refresh(nonLazyInitBeans);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /*TODO:完成完整的refresh逻辑,可以参考https://www.cnblogs.com/hellowhy/p/15618896.html或者
       https://blog.csdn.net/qq_29799655/article/details/105398225*/
    public void refresh(List<BeanDefinition>nonLazyInitBeans) throws BeansException {
        registerBeanPostProcessors(this.beanFactory);
        this.beanFactory.preInstantiateSingletons(nonLazyInitBeans);
    }
    //context再对外提供一个getBean，底下就是调用的BeanFactory对应的方法
    private void registerBeanPostProcessors(AutowireCapableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

    //TODO
    /*public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor
                                                    postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }*/
    public Object getBean(String beanName) throws BeansException {
        return this.beanFactory.getBean(beanName);
    }
    public Boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }
    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }
    public void publishEvent(ApplicationEvent event) {
    }
    public boolean isSingleton(String name) {
        return false;
    }
    public boolean isPrototype(String name) {
        return false;
    }
    public Class<?> getType(String name) {
        return null;
    }
}