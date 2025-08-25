package minis.context;

import minis.beans.BeansException;
import minis.beans.factory.config.ConfigurableBeanFactory;
import minis.beans.factory.config.ConfigurableListableBeanFactory;
import minis.beans.factory.ListableBeanFactory;
import minis.core.env.Environment;
import minis.core.env.EnvironmentCapable;

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
