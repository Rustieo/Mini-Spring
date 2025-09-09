package com.minis.aop;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.FactoryBean;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.utils.ClassUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//调用过程:BeanFactory的getBean发现是FactoryBean对象后,会调用getObject方法
//如果我们想实现代理,可以在getObject方法中生成代理对象,通过利用AopProxyFactory和target(被代理的对象)
//最终返回的是代理对象
@Data
public class ProxyFactoryBean implements FactoryBean<Object> {
    private DefaultListableBeanFactory beanFactory;
    private AopProxyFactory aopProxyFactory;
    //这里的interceptorName在Spring中是Advice或者Advisor的名称,叫interceptorName
    //是因为历史遗留问题
    //在MiniSpring中指代Advisor
    private String []interceptorNames;
    private String targetName;
    private Object target;
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private Object singletonInstance;
    private List<PointcutAdvisor> advisors;

    public ProxyFactoryBean() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }


    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        this.aopProxyFactory = aopProxyFactory;
    }
    public AopProxyFactory getAopProxyFactory() {
        return this.aopProxyFactory;
    }

    public void setAdvisors(List<PointcutAdvisor> advisors) {
        this.advisors = advisors;
    }

    public void setInterceptorNames(String[] interceptorNames) {
        this.interceptorNames = interceptorNames;
    }
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
    public Object getTarget() {
        return target;
    }
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object getObject() throws Exception {
        initializeAdvisor();
        return getSingletonInstance();
    }

    private synchronized void initializeAdvisor() {
        Object interceptorBean = null;
//		MethodInterceptor mi = null;
        Map<String, PointcutAdvisor> beanMap = beanFactory.getBeansOfType(PointcutAdvisor.class);
        interceptorNames=beanMap.keySet().toArray(new String[0]);
        this.advisors=new ArrayList<>();
        for (String interceptorName : interceptorNames) {
            try {
                interceptorBean = this.beanFactory.getBean(interceptorName);
            } catch (BeansException e) {
                e.printStackTrace();
            }
            //下面这几行段逻辑勉强跟Spring的差不多
            if(interceptorBean instanceof PointcutAdvisor) {
                this.advisors.add((PointcutAdvisor) interceptorBean);
            }else if(interceptorBean instanceof Advice) {
                this.advisors.add(new NameMatchMethodPointcutAdvisor((Advice) interceptorBean));
            }
        }
//		if (advice instanceof BeforeAdvice) {
//			mi = new MethodBeforeAdviceInterceptor((MethodBeforeAdvice)advice);
//		}
//		else if (advice instanceof AfterAdvice){
//			mi = new AfterReturningAdviceInterceptor((AfterReturningAdvice)advice);
//		}
//		else if (advice instanceof MethodInterceptor) {
//			mi = (MethodInterceptor)advice;
//		}

        //advisor = new NameMatchMethodPointcutAdvisor((Advice)advice);
        //advisor.setMethodInterceptor(mi);

    }

    private synchronized Object getSingletonInstance() {
        if (this.singletonInstance == null) {
            this.singletonInstance = getProxy(createAopProxy());
        }
        return this.singletonInstance;
    }
    protected AopProxy createAopProxy() {
        return getAopProxyFactory().createAopProxy(target,advisors);
    }
    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy();
    }
    @Override
    public Class<?> getObjectType() {
        return null;
    }
}