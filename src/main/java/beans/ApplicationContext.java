package beans;

import env.Environment;
import env.EnvironmentCapable;

public interface ApplicationContext
        extends EnvironmentCapable, ListableBeanFactory,
        ConfigurableBeanFactory, ApplicationEventPublisher{
    String getApplicationName();
    long getStartupDate();
    ConfigurableListableBeanFactory getBeanFactory() throws
            IllegalStateException;
    void setEnvironment(Environment environment);
    Environment getEnvironment();
    //void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);
    void refresh() throws BeansException, IllegalStateException;
    void close();
    boolean isActive();
}
