package com.minis.beans.factory.support;

import com.minis.beans.factory.ObjectFactory;
import com.minis.beans.factory.config.SingletonBeanRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    //容器中存放所有bean的名称的列表
    protected List<String> beanNames = new ArrayList<>();
    protected List<String> earlyBeanNames = new ArrayList<>();
    //容器中存放所有bean实例的map
    protected Map<String, Object> singletons = new ConcurrentHashMap<>(256);
    protected Map<String, Object> earlySingletons = new ConcurrentHashMap<>(256);
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletons) {
            Object oldObject = this.singletons.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object [" + singletonObject +
                        "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
            }
            addSingleton(beanName,singletonObject);
        }
    }
    public void addSingleton(String beanName, Object singletonObject){
        synchronized (this.singletons) {
            this.singletons.put(beanName, singletonObject);
            this.earlySingletons.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.beanNames.add(beanName);
        }
    }
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (this.singletons) {
            if (!this.singletons.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletons.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }
    public void registerEarlySingleton(String beanName, Object earlySingletonObject) {
        synchronized (this.earlySingletons) {
            this.earlySingletons.put(beanName, earlySingletonObject);
            this.earlyBeanNames.add(beanName);
        }
    }

    public Object getSingleton(String beanName) {
        Object singletonObject=this.singletons.get(beanName);
        if(singletonObject==null){
            singletonObject=this.earlySingletons.get(beanName);
            if(singletonObject==null){
                synchronized (this.singletons){
                    singletonObject=this.singletons.get(beanName);
                    if(singletonObject==null){
                        singletonObject=this.earlySingletons.get(beanName);
                        if(singletonObject==null){
                            ObjectFactory<?> objectFactory=this.singletonFactories.get(beanName);
                            if(objectFactory!=null){
                                singletonObject=objectFactory.getObject();
                                this.singletonFactories.remove(beanName);
                                this.earlySingletons.put(beanName,singletonObject);
                            }
                        }
                    }
                }
            }
        }
        return singletonObject;
    }
    public Object  getSingleton(String beanName,ObjectFactory<?>singletonFactory){
        synchronized (this.singletons){
            Object singletonObject=this.singletons.get(beanName);
            if(singletonObject==null){
                //实际执行的是createBean
                singletonObject=singletonFactory.getObject();
            }
            addSingleton(beanName,singletonObject);
            return singletonObject;
        }
    }
    /*public Object getEarlySingleton(String beanName) {
        return this.earlySingletons.get(beanName);
    }*/
    public String[] getDependentBeans(String beanName) {
        return null;
    }
    public boolean containsSingleton(String beanName) {
        return this.singletons.containsKey(beanName);
    }
    public boolean containsEarlySingleton(String beanName) {
        return this.earlySingletons.containsKey(beanName);
    }
    public String[] getSingletonNames() {
        return (String[]) this.beanNames.toArray();
    }
    public String[] getEarlySingletonNames() {
        return (String[]) this.earlyBeanNames.toArray();
    }
    protected void removeSingleton(String beanName) {
        synchronized (this.singletons) {
            this.beanNames.remove(beanName);
            this.singletons.remove(beanName);
        }
    }
    protected void removeEarlySingleton(String beanName) {
        synchronized (this.earlySingletons) {
            this.earlyBeanNames.remove(beanName);
            this.earlySingletons.remove(beanName);
        }
    }
}
