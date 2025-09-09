package com.minis.utils;

import com.minis.aop.Advisor;
import com.minis.aop.Pointcut;
import com.minis.aop.PointcutAdvisor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class AopUtils {
    //判断一个切点与一个类是否匹配,如果匹配,说明这个类需要被SpringAop代理
    public static boolean canApply(Pointcut pc, Class<?> targetClass) {
        //先检查类过滤器
        if(!pc.getClassFilter().matches(targetClass)){
            return false;
        }
        /*if(pc.getMethodMatcher().equals(MethodMatcher.TRUE)){
            return true;
        }*/
        //这个数组用于收集targetClass的所有父类,接口类等
        List<Class<?>> classes=new ArrayList<>();
        //如果targetClass不是代理类,则将targetClass添加到classes
        if(!Proxy.isProxyClass(targetClass)){
            //关于这里为什么是ClassUtils.getUserClass(targetClass),见笔记
            classes.add(ClassUtils.getUserClass(targetClass));
        }
        //把接口和父类都加到里面去
        classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
        //遍历classes,检查每个类是否匹配
        for(Class<?> clazz:classes){
            //逻辑:获取每个类中所有的方法,检查是否与切点中匹配
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
            for(Method method:methods){
                //TODO:有时间的话搞明白为啥下面的参数是targetClass,而不是clazz
                if(pc.getMethodMatcher().matches(method,targetClass)){
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean canApply(Advisor advisor, Class<?> targetClass) {
        if(advisor instanceof PointcutAdvisor){
            PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
            return canApply(pointcutAdvisor.getPointcut(),targetClass);
        }
        return false;
    }
}
