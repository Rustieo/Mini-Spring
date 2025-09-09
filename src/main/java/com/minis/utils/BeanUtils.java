package com.minis.utils;

import com.minis.beans.factory.ListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;

import java.beans.Introspector;

public class BeanUtils {
    public static String convertClassName(String className) {
        return Introspector.decapitalize(className);
    }
    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type) {
        String[] beanNames = lbf.getBeanNamesForType(type);
        if(beanNames.length==0){
            //TODO:这里逻辑跟原来的差的有一丢丢多
            if(lbf instanceof DefaultListableBeanFactory){
                DefaultListableBeanFactory parent=(DefaultListableBeanFactory) ((DefaultListableBeanFactory) lbf).getParentBeanFactory();
                if(parent!=null){
                    beanNames=parent.getBeanNamesForType(type);
                }
            }
        }
        return beanNames;
    }
    public static Object[] getBeanObjectsForTypeIncludingAncestors(ListableBeanFactory lbf, Class<?> type) {
        String[] beanNames = beanNamesForTypeIncludingAncestors(lbf, type);
        Object[] beanObjects = new Object[beanNames.length];
        for (int i = 0; i < beanNames.length; i++) {
            beanObjects[i] = lbf.getBean(beanNames[i]);
        }
        return beanObjects;
    }
}
