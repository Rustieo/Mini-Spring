package beans.factory;

public interface BeanFactory {
    Object getBean(String beanName) throws BeansException;
    void registerBean(String beanName, Object obj);
    boolean containsBean(String name);
    boolean isSingleton(String name);
    boolean isPrototype(String name);
    Class<?> getType(String name);
}