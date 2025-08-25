package minis.beans.factory.support;

import minis.beans.factory.config.SingletonBeanRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    //容器中存放所有bean的名称的列表
    protected List<String> beanNames = new ArrayList<>();
    protected List<String> earlyBeanNames = new ArrayList<>();
    //容器中存放所有bean实例的map
    protected Map<String, Object> singletons = new ConcurrentHashMap<>(256);
    protected Map<String, Object> earlySingletons = new ConcurrentHashMap<>(256);

    public void registerSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletons) {
            this.singletons.put(beanName, singletonObject);
            this.beanNames.add(beanName);
        }
    }

    public void registerEarlySingleton(String beanName, Object earlySingletonObject) {
        synchronized (this.earlySingletons) {
            this.earlySingletons.put(beanName, earlySingletonObject);
            this.earlyBeanNames.add(beanName);
        }
    }
    public Object getSingleton(String beanName) {
        return this.singletons.get(beanName);
    }
    public Object getEarlySingleton(String beanName) {
        return this.earlySingletons.get(beanName);
    }
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
