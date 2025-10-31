package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.InitializingBean;
import com.minis.beans.factory.config.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        boolean earlySingletonExposure = (beanDefinition.isSingleton() && this.allowCircularReferences);
        addSingletonFactory(beanDefinition.getId(), () -> getEarlyBeanReference(beanDefinition.getId(), bean));
        //NOTE 关于这里为什么要定义一个exposedObject,首先概念是:exposedObject是最终结果(即res),而我们又用bean保存了原始毛坯
        //至于为什么这么做,见Spring的AbstractAutowireCapableBeanFactory的632行
        Object  exposedObject=bean;
        // 处理属性
        populateBean(beanDefinition, clz, bean);
        //如果发生了循环依赖并且有AOP,那么这里的exposedObject应为未被代理的原始对象
        exposedObject=initializeBean(beanDefinition, bean);
        if(earlySingletonExposure){
            Object earlySingletonReference=getSingleton(beanDefinition.getId());
            if(earlySingletonReference!=null){//说明发生循环依赖
                if(exposedObject==bean){
                    exposedObject=earlySingletonReference;//把代理对象赋值给exposedObject
                }else {
                    throw new BeansException(
                            "Bean with name '" + beanDefinition.getId() + "' has been injected into other beans "+
                                    "in its raw version as part of a circular reference, but has eventually been " +
                                    "wrapped. This means that said other beans do not use the final version of the " +
                                    "bean. This is often the result of over-eager type matching - consider using " +
                                    "'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
                }
            }
        }
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
    protected void populateBean(BeanDefinition beanDefinition, Class<?> clz, Object obj){
        //执行xml注入
        //NOTE Spring中其实没handleProperties这个方法
        handleProperties(beanDefinition, clz, obj);
        //执行注解注入
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                try {
                    ((InstantiationAwareBeanPostProcessor) bp).postProcessProperties(null, obj, beanDefinition.getId());
                } catch (BeansException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        // 处理属性
        PropertyValues propertyValues = bd.getPropertyValues();
        //如果有属性
        if (!propertyValues.isEmpty()) {
            for (int i=0; i<propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String pName = propertyValue.getName();
                String pType = propertyValue.getType();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.isRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                if (!isRef) { //如果不是ref，只是普通属性
                    //对每一个属性，分数据类型分别处理
                    if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                        paramTypes[0] = String.class;
                    } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                        paramTypes[0] = Integer.class;
                    } else if ("int".equals(pType)) {
                        //TODO 这里好像有问题
                        paramTypes[0] = int.class;
                    } else {
                        paramTypes[0] = String.class;
                    }
                    paramValues[0] = pValue;
                } else { //is ref, create the dependent beans
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    //再次调用getBean创建ref的bean实例
                    try {
                        paramValues[0] = getBean((String) pValue);
                    } catch (BeansException e) {
                        throw new RuntimeException(e);
                    }
                }
                //按照setXxxx规范查找setter方法，调用setter方法设置属性
                String methodName = "set" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
                Method method = null;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                    method.invoke(obj, paramValues);
                } catch (NoSuchMethodException e) {
                    log.error("set方法不存在,{}", methodName);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }catch (IllegalArgumentException e){
                    log.error(methodName);
                    throw new RuntimeException(e);
                }

            }
        }
    }
    public Object initializeBean(BeanDefinition beanDefinition, Object bean){
        Object wrappedBean = bean;
        // step 1: postProcessBeforeInitialization
        wrappedBean=applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanDefinition.getId());
        // step 2: 调用setter方法
        try{
            invokeInitMethods(beanDefinition, wrappedBean);
        }
        catch (Throwable ex) {
            throw new BeansException("Invocation of init method failed");
        }

        // step 3: Autowired注入
        wrappedBean=applyBeanPostProcessorsAfterInitialization(wrappedBean,beanDefinition.getId());
        return wrappedBean;
    }
    private void invokeInitMethods(BeanDefinition beanDefinition, Object bean) throws Throwable {
        if(bean instanceof InitializingBean){
            ((InitializingBean) bean).afterPropertiesSet();
        }
        if (beanDefinition.getInitMethodName() != null && !beanDefinition.equals("")) {
            Class<?> clz = beanDefinition.getClass();
            Method method = null;
            try {
                method = clz.getMethod(beanDefinition.getInitMethodName());
                method.invoke(bean);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
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
            beanPostProcessor.setBeanFactory(this);
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
