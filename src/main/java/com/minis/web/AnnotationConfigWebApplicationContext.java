package com.minis.web;

import com.minis.Utils.BeanUtil;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.context.*;
import jakarta.servlet.ServletContext;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


//这个ApplicationContext与ClassPathXmlApplicationContext不同的是,后者会扫描xml文件,把xml文件中定义的构造器参数等放到BeanDefinition中
//而前者只是单纯根据类名创建一个空的BeanDefinition,因此Controller目前不支持构造器参数啥的,你可以自己在initController()中完善
public class AnnotationConfigWebApplicationContext extends AbstractApplicationContext implements WebApplicationContext{
    private WebApplicationContext parentApplicationContext;
    private ServletContext servletContext;
    DefaultListableBeanFactory beanFactory;
    //TODO
/*    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors =
            new ArrayList<BeanFactoryPostProcessor>();*/

    public AnnotationConfigWebApplicationContext(String fileName) {
        this(fileName, null);
    }
    public AnnotationConfigWebApplicationContext(String fileName, WebApplicationContext parentApplicationContext) {
        this.parentApplicationContext = parentApplicationContext;
        this.servletContext = this.parentApplicationContext.getServletContext();
        URL xmlPath = null;
        try {
            xmlPath = this.getServletContext().getResource(fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        List<String> packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);
        List<String> controllerNames = scanPackages(packageNames);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        this.beanFactory = bf;
        this.beanFactory.setParent(this.parentApplicationContext.getBeanFactory());
        //把扫描到的Controller注册到beanFactory中
        loadBeanDefinitions(controllerNames);

        if (true) {
            try {
                refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void loadBeanDefinitions(List<String> controllerNames) {
        for (String controller : controllerNames) {
            //把类名的首字母改为小写，作为bean的id
            String beanID= BeanUtil.convertClassName(controller);
            String beanClassName=controller;
            BeanDefinition beanDefinition=new BeanDefinition(beanID,beanClassName);
            this.beanFactory.registerBeanDefinition(beanID,beanDefinition);
        }
    }
    private List<String> scanPackages(List<String> packages) {
        List<String> tempControllerNames = new ArrayList<>();
        for (String packageName : packages) {
            tempControllerNames.addAll(scanPackage(packageName));
        }
        return tempControllerNames;
    }
    private List<String> scanPackage(String packageName) {
        List<String> tempControllerNames = new ArrayList<>();
        URL url  =this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if(file.isDirectory()){
                scanPackage(packageName+"."+file.getName());
            }else{
                String controllerName = packageName +"." +file.getName().replace(".class", "");
                tempControllerNames.add(controllerName);
            }
        }
        return tempControllerNames;
    }
    public void setParent(WebApplicationContext parentApplicationContext) {
        this.parentApplicationContext = parentApplicationContext;
        this.beanFactory.setParent(this.parentApplicationContext.getBeanFactory());
    }
    public ServletContext getServletContext() {
        return this.servletContext;
    }
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }
    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }
    public void registerListeners() {
        ApplicationListener listener = new ApplicationListener();
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }
    public void initApplicationEventPublisher() {
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) {
    }
    public void registerBeanPostProcessors(ConfigurableListableBeanFactory bf) {
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }
    public void onRefresh() {

    }
    @Override
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory.preInstantiateSingletons();
    }
    public void finishRefresh() {
    }
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }
}