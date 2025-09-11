package com.minis.aop;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.FactoryBean;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.utils.ClassUtils;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

//调用过程:BeanFactory的getBean发现是FactoryBean对象后,会调用getObject方法
//如果我们想实现代理,可以在getObject方法中生成代理对象,通过利用AopProxyFactory和target(被代理的对象)
//最终返回的是代理对象
@Data
@Slf4j
public class ProxyFactoryBean extends ProxyCreatorSupport implements FactoryBean<Object> {
    private DefaultListableBeanFactory beanFactory;
    //这里的interceptorName在Spring中是Advice或者Advisor的名称,叫interceptorName
    //是因为历史遗留问题
    //在MiniSpring中指代Advisor
    @Setter
    private String[] interceptorNames;
    @Setter
    private String targetName;
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private Object singletonInstance;

    public ProxyFactoryBean() {
        super();
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object getObject() {
        //initializeAdvisor();
        return getSingletonInstance();
    }
    //FIXME 逻辑感觉有问题,没有检查advisor是否匹配
    private synchronized void initializeAdvisor() {
        Object interceptorBean = null;
        Map<String, PointcutAdvisor> beanMap = beanFactory.getBeansOfType(PointcutAdvisor.class);
        interceptorNames = beanMap.keySet().toArray(new String[0]);

        try {
            setTarget(beanFactory.getBean(targetName));
        } catch (BeansException e) {
            throw new RuntimeException("Target bean '" + targetName + "' not found", e);
        }

        for (String interceptorName : interceptorNames) {
            try {
                interceptorBean = this.beanFactory.getBean(interceptorName);
            } catch (BeansException e) {
                log.error("Cannot get advisor bean: {}", interceptorName, e);
            }
            //下面这几行段逻辑勉强跟Spring的差不多
            if (interceptorBean instanceof PointcutAdvisor) {
                addAdvisor((PointcutAdvisor) interceptorBean);
            } else if (interceptorBean instanceof Advice) {
                addAdvisor(new NameMatchMethodPointcutAdvisor((Advice) interceptorBean));
            }
        }
    }

    private synchronized Object getSingletonInstance() {
        if (this.singletonInstance == null) {
            this.singletonInstance = getProxy(createAopProxy());
        }
        return this.singletonInstance;
    }

    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy();
    }

    @Override
    public Class<?> getObjectType() {
        if (getTarget() == null) {
            return null;
        }
        return getTarget().getClass();
    }
}