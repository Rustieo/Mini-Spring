package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.beans.BeansException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();
    //XXX:这个先不管
    private boolean allowCircularReferences = true;
    public AbstractAutowireCapableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
    }

    public AbstractAutowireCapableBeanFactory() {
        super();
    }
    public Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        //先把bean实例化,生成原胚并放入工厂
        Object bean = createBeanInstance(beanDefinition);
        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        addSingletonFactory(beanDefinition.getId(), () -> getEarlyBeanReference(beanDefinition.getId(), bean));
        //NOTE 关于这里为什么要定义一个exposedObject,首先概念是:exposedObject是最终结果(即res),而我们又用bean保存了原始毛坯
        //至于为什么这么做,见Spring的AbstractAutowireCapableBeanFactory的632行
        Object  exposedObject=bean;
        // 处理属性
        populateBean(beanDefinition, clz, bean);
        exposedObject=initializeBean(beanDefinition, bean);
        return exposedObject;
    }
    public Object createBeanInstance(BeanDefinition beanDefinition){
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;
        try {
            clz = Class.forName(beanDefinition.getClassName());
            // 处理构造器参数
            ArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();
            //如果有参数
            if (!argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];
                //对每一个参数，分数据类型分别处理
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(argumentValue.getType()) ||
                            "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    } else if ("Integer".equals(argumentValue.getType()) ||
                            "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] =
                                Integer.valueOf((String)argumentValue.getValue());
                    } else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String)
                                argumentValue.getValue());
                    } else { //默认为string
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }
                //按照特定构造器创建实例
                con = clz.getConstructor(paramTypes);
                obj = con.newInstance(paramValues);
            } else { //如果没有参数，直接创建实例
                obj = clz.newInstance();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //把bean的class:beanName[]放入缓存
        //TODO 这里要修改下,不是所有的bean都要放进去,而且我感觉并不是bean实例化后就立即放进去
        if(allBeanNamesByType.containsKey(clz)){
            allBeanNamesByType.get(clz)[allBeanNamesByType.get(clz).length] = beanDefinition.getId();
        }else {
            allBeanNamesByType.put(clz, new String[]{beanDefinition.getId()});
        }
        return obj;
    }
    public Object initializeBean(BeanDefinition beanDefinition, Object bean){
        Object wrappedBean = bean;
        // step 1: postProcessBeforeInitialization
        wrappedBean=applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanDefinition.getId());
        // step 2: 调用setter方法
        if (beanDefinition.getInitMethodName() != null && !beanDefinition.equals("")) {
            invokeInitMethods(beanDefinition, bean);
        }
        // step 3: Autowired注入
        wrappedBean=applyBeanPostProcessorsAfterInitialization(wrappedBean,beanDefinition.getId());
        return wrappedBean;
    }
    private void invokeInitMethods(BeanDefinition beanDefinition, Object obj) {
        Class<?> clz = beanDefinition.getClass();
        Method method = null;
        try {
            method = clz.getMethod(beanDefinition.getInitMethodName());
            method.invoke(obj);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
    protected Object getEarlyBeanReference(String beanName,Object bean) {
        Object exposedObject = bean;
            for (BeanPostProcessor bp : getBeanPostProcessors()) {
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    exposedObject = ((SmartInstantiationAwareBeanPostProcessor)bp).getEarlyBeanReference(exposedObject, beanName);
                }
            }
        return exposedObject;
    }

    //TODO 这个方法的逻辑有点混乱,好多类都各自实现了
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        if(!beanPostProcessors.contains(beanPostProcessor)){
            beanPostProcessors.add(beanPostProcessor);
        }
    }
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        Object current;
        //下面是管道设计模式
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            //TODO:感觉setBeanFactory应该是在注册那里调用的
            beanProcessor.setBeanFactory(this);
            current = beanProcessor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            result = beanProcessor.postProcessAfterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }
}
