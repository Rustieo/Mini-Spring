package com.minis.beans.factory.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;


@Slf4j
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    private DefaultListableBeanFactory beanFactory;

    //TODO 这里的注入逻辑实际上对应的是 postProcessProperties方法而不是下面这个,有时间的话改改
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object result = bean;

        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        if(fields!=null){
            //对每一个属性进行判断，如果带有@Autowired注解则进行处理
            for(Field field : fields){
                boolean isAutowired = field.isAnnotationPresent(Autowired.class);
                if(isAutowired){
                    Object autowiredObj = null;
                    //先根据类型查找(byType)
                    Class<?> fieldType = field.getType();
                    String[] beanNames = this.beanFactory.getBeanNamesByType(fieldType);
                    if(beanNames==null){//说明可能是接口,我们xml中只注册了实现类,声明时却用了接口
                        throw new BeansException("对应类型的Bean不存在,类型:"+fieldType);
                    }
                    else if(beanNames.length==1){
                        autowiredObj = this.getBeanFactory().getBean(beanNames[0]);
                        //log.info("正在按照类型查找,类型:{}, Bean名称:{}",field.getType(),beanNames[0]);
                    }else {
                        //存在多个类型兼容的 Bean,比如一个接口的多个实现类,或者bean不是单例,或者有多个单例bean等等.需要通过名称来解决歧义
                        String fieldName = field.getName();
                        autowiredObj = this.getBeanFactory().getBean(fieldName);
                        //log.info("正在按照名称查找,Bean名称:{}",fieldName);
                        //TODO:更完善的实现见https://blog.csdn.net/qq_20867219/article/details/108053188
                    }
                    if(autowiredObj==null){
                        throw new BeansException("对应名称的Bean不存在");
                    }
                    //设置属性值，完成注入
                    try {
                        field.setAccessible(true);
                        field.set(bean, autowiredObj);
                        //log.info("autowire {} for bean {}", field.getName(), beanName);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return result;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }
}
