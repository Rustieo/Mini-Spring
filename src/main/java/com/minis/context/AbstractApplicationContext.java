package com.minis.context;


import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.core.env.Environment;


import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext implements ApplicationContext {
    private Environment environment;
    DefaultListableBeanFactory beanFactory;
    //private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    private long startupDate;
    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private ApplicationEventPublisher applicationEventPublisher;
    @Override
    public Object getBean(String beanName) throws BeansException {
        return getBeanFactory().getBean(beanName);
    }
    //public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {return this.beanFactoryPostProcessors;}
    /*TODO:完成完整的refresh逻辑,可以参考https://www.cnblogs.com/hellowhy/p/15618896.html或者
       https://blog.csdn.net/qq_29799655/article/details/105398225*/
    public void refresh() throws BeansException, IllegalStateException {
        postProcessBeanFactory(getBeanFactory());
        registerBeanPostProcessors(getBeanFactory());
        initApplicationEventPublisher();
        //onRefresh:模板方法，具体的子类可以在这里初始化一些特殊的 Bean（在初始化 singleton beans 之前）
        onRefresh();
        registerListeners();
        finishBeanFactoryInitialization(beanFactory);
        finishRefresh();
    }
    protected abstract void registerListeners();
    protected abstract void initApplicationEventPublisher();
    protected abstract void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
    protected abstract void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory);
    protected abstract void onRefresh();
    protected abstract void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory);
    protected abstract void finishRefresh();
    @Override
    public String getApplicationName() {
        return "";
    }
    @Override
    public long getStartupDate() {
        return this.startupDate;
    }
    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    @Override
    public void close() {
    }
    @Override
    public boolean isActive(){
        return true;
    }
    //重写父类的方法
    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        getBeanFactory().registerSingleton(beanName, singletonObject);
    }

    @Override
    public Object getSingleton(String beanName) {
        return getBeanFactory().getSingleton(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return getBeanFactory().containsSingleton(beanName);
    }

    @Override
    public String[] getSingletonNames() {
        return getBeanFactory().getSingletonNames();
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return getBeanFactory().containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        return getBeanFactory().getBeanNamesForType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        getBeanFactory().addBeanPostProcessor(beanPostProcessor);

    }

    @Override
    public int getBeanPostProcessorCount() {
        return getBeanFactory().getBeanPostProcessorCount();
    }





    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }



    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }
    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }
    @Override
    public boolean isSingleton(String name) {
        return getBeanFactory().isSingleton(name);
    }
    @Override
    public boolean isPrototype(String name) {
        return getBeanFactory().isPrototype(name);
    }
    @Override
    public Class<?> getType(String name) {
        return getBeanFactory().getType(name);
    }


}