package com.minis.aop;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.utils.AopUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class ProxyCreator implements BeanPostProcessor {
    protected static final Object[] DO_NOT_PROXY = null;
    private String pattern;
    //记录的是全局拦截器,不包括特定bean的拦截器
    String[] interceptorNames;
    private AopProxyFactory aopProxyFactory;
    DefaultListableBeanFactory beanFactory;
    List<Advisor> advisorsCache;
    public ProxyCreator(){
        //TODO 因为目前xml还没定义数组,因此只能先这么搞了(笑)
        this.aopProxyFactory = new DefaultAopProxyFactory();
        this.interceptorNames=new String[]{"myInterceptor"};
    }
    public ProxyCreator(String[] interceptorNames) {
        this.interceptorNames = interceptorNames;
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //获取一个Bean的所有Advisor
        Object[] advisors = getAdvicesAndAdvisorsForBean(beanName,bean.getClass());
        if(advisors==DO_NOT_PROXY){
            return bean;
        }
        log.info("AOP匹配成功,Bean名称:{}",beanName);
        //创建代理
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(bean);
        proxyFactoryBean.setBeanFactory(this.beanFactory);
        List<PointcutAdvisor>advisorsList=new ArrayList<>();
        for (Object advisor : advisors) {
            advisorsList.add((PointcutAdvisor) advisor);
        }
        //proxyFactoryBean.setAdvisors(advisorsList);
        proxyFactoryBean.setAopProxyFactory(this.aopProxyFactory);
        return proxyFactoryBean;
    }
    //获取一个Bean的所有Advisor
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
        List<Advisor> eligibleAdvisors = new ArrayList<>();
        for (Advisor advisor : candidateAdvisors) {
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                if(pointcutAdvisor instanceof PointcutAdvisor){


                }
                //检查切点和该类是否匹配
                if(AopUtils.canApply(pointcutAdvisor,beanClass)){
                    //匹配成功,说明该类有需要被代理的方法(minis目前只能实现方法的代理)
                    eligibleAdvisors.add(advisor);
                }
            }
        }
        return eligibleAdvisors;
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
