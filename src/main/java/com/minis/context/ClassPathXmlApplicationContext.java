package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.Resource;
import com.minis.beans.factory.ClassPathXmlResource;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.beans.xml.XmlBeanDefinitionReader;
import com.minis.utils.BeanUtils;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    //TODO 这里接口的声明感觉有点随便,可以挑个更合适的
    DefaultListableBeanFactory beanFactory=new DefaultListableBeanFactory();
    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }
    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        Resource resource = new ClassPathXmlResource(fileName);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        if (isRefresh) {
            try {
                this.refresh();
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
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


    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }


    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }


    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }
    //XXX 目前这里是手动注入,后续可以考虑自动注入
    @Override
    public void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Object[] beanPostProcessors = BeanUtils.getBeanObjectsForTypeIncludingAncestors(beanFactory, BeanPostProcessor.class);
        //XXX:这里的AutowiredAnnotationBeanPostProcessor是手动添加的,后续可以考虑自动添加
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
        for(Object beanPostProcessor:beanPostProcessors){
            this.beanFactory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
        }

    }
    public void initApplicationEventPublisher(){
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }

    @Override
    protected void onRefresh() {

    }
    @Override
    protected void registerListeners() {

    }

    @Override
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory.preInstantiateSingletons();
        System.out.println();
    }

    @Override
    protected void finishRefresh() {

    }




}