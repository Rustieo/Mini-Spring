package beans.factory.xml;

import beans.BeansException;
import beans.Resource;
import beans.factory.ApplicationEvent;
import beans.factory.support.SimpleBeanFactory;

public class ClassPathXmlApplicationContext {
    SimpleBeanFactory beanFactory;
    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }
    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        Resource resource = new ClassPathXmlResource(fileName);
        SimpleBeanFactory simpleBeanFactory = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(simpleBeanFactory);
        reader.loadBeanDefinitions(resource);
        this.beanFactory = simpleBeanFactory;
        if (isRefresh) {
            this.beanFactory.refresh();
        }
    }
    //context再对外提供一个getBean，底下就是调用的BeanFactory对应的方法
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