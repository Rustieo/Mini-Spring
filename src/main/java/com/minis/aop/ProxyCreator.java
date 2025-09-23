package com.minis.aop;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.FactoryBean;
import com.minis.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.utils.AopUtils;
import com.minis.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class ProxyCreator implements SmartInstantiationAwareBeanPostProcessor {
    protected static final Object[] DO_NOT_PROXY = null;
    private String pattern;
    //记录的是全局拦截器,不包括特定bean的拦截器
    String[] interceptorNames;
    private AopProxyFactory aopProxyFactory;
    DefaultListableBeanFactory beanFactory;
    List<Advisor> advisorsCache;
    //用于缓存在循环依赖中提前被代理的对象
    private final Map<Object, Object> earlyProxyReferences = new ConcurrentHashMap<>(16);
    public ProxyCreator(){
        //TODO 因为目前xml还没定义数组,因此只能先这么搞了(笑)
        this.aopProxyFactory = new DefaultAopProxyFactory();
        this.interceptorNames=new String[]{"myInterceptor"};
    }
    public ProxyCreator(String[] interceptorNames) {
        this.interceptorNames = interceptorNames;
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return pvs;
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean != null) {
            //检查这个bean是否在解决循环依赖时已经被代理过了
            Object cacheKey = getCacheKey(bean.getClass(), beanName);
            if (this.earlyProxyReferences.remove(cacheKey) != bean) {
                return wrapIfNecessary(bean, beanName, cacheKey);
            }
        }
        return bean;
    }

    public Object wrapIfNecessary(Object bean,String beanName,Object cacheKey){
        //跳过AOP基础设施类，不进行代理
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }
        //获取一个Bean的所有Advisor
        Object[] advisors = getAdvicesAndAdvisorsForBean(beanName,bean.getClass());
        if(advisors==DO_NOT_PROXY){
            return bean;
        }
        log.info("AOP匹配成功,Bean名称:{}",beanName);
        //创建代理
        List<PointcutAdvisor>advisorsList=new ArrayList<>();
        for (Object advisor : advisors) {
            advisorsList.add((PointcutAdvisor) advisor);
        }
        /*ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(bean);
        proxyFactoryBean.setBeanFactory(this.beanFactory);
        //proxyFactoryBean.setAdvisors(advisorsList);
        proxyFactoryBean.setAopProxyFactory(this.aopProxyFactory);*/
        ProxyFactory proxyFactory=new ProxyFactory(bean);
        proxyFactory.setAdvisors(advisorsList);
        proxyFactory.setAopProxyFactory(this.aopProxyFactory);
        return proxyFactory.getProxy();
    }


    //获取一个Bean最终要用的Advisor
    private Object[] getAdvicesAndAdvisorsForBean(String beanName,Class<?> beanClass) {
        List<Advisor> advisors = findEligibleAdvisors(beanName,beanClass);
        return advisors.isEmpty() ? DO_NOT_PROXY:advisors.toArray(new Advisor[0]);
    }
    private List<Advisor> findEligibleAdvisors(String beanName, Class<?> beanClass){
        List<Advisor> candidateAdvisors = findCandidateAdvisors(beanName,beanClass);
        List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors,beanClass);
        return eligibleAdvisors;
    }
    //获取作用于特定Bean的动态的Advisor(在Spring中)
    //然后另一个方法:resolveInterceptorNames是用来获取配置文件中的advisor,是全局的,固定好的
    //但是minis只能通过配置文件来注册Advisor(笑)
    private List<Advisor> findCandidateAdvisors(String beanName,Class<?> beanClass) {
        if (advisorsCache != null) {
            return advisorsCache;
        }
        List<Advisor> advisors=new ArrayList<>();
        advisors.addAll(this.beanFactory.getBeansOfType(Advisor.class).values());
        advisorsCache=advisors;
        return advisors;
    }
    //判断Advisor是否适用于特定的Bean
    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass) {
        return AopUtils.findAdvisorsThatCanApply(candidateAdvisors,beanClass);
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        // 跳过AOP基础设施类，避免在收集Advisor时再次走代理导致递归
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        this.earlyProxyReferences.put(cacheKey, bean);
        return wrapIfNecessary(bean, beanName, cacheKey);
    }

    protected Object getCacheKey(Class<?> beanClass,String beanName) {
        if (StringUtils.hasLength(beanName)) {
            return (FactoryBean.class.isAssignableFrom(beanClass) ?
                    BeanFactory.FACTORY_BEAN_PREFIX + beanName : beanName);
        }
        else {
            return beanClass;
        }
    }
    public boolean isInfrastructureClass(Class<?> beanClass) {
        boolean retVal = Advice.class.isAssignableFrom(beanClass) ||
                Pointcut.class.isAssignableFrom(beanClass) ||
                Advisor.class.isAssignableFrom(beanClass);
        return retVal;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
