package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.FactoryBean;
import com.minis.beans.factory.config.BeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements BeanFactory, BeanDefinitionRegistry {
    private BeanFactory parentBeanFactory;
    //改成了protected
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    protected List<String> beanDefinitionNames = new ArrayList<>();
    protected Map<Class,String[]> allBeanNamesByType = new ConcurrentHashMap<>();
    List<BeanDefinition>nonLazyInitBeans =new ArrayList<>();

    public AbstractBeanFactory(BeanFactory parentBeanFactory){
        this.parentBeanFactory=parentBeanFactory;
    }

    public AbstractBeanFactory() {

    }
    /*普通bean创建逻辑:(AbstractBeanFactory为ab,AutowiredAbstractBeanFactory为aab,DefaultBeanRegistry为dbr
    * ab:getBean->dbr:getSingleton(查询缓存版)->不在缓存中->ab:getSingleton(传入工厂对象)->调用工厂对象的getObject()
    * aab:createBean->aab:createBeanInstance调用构造器创建原胚->aab:addSingletonFactory,把工厂对象放入缓存,同时这里工厂的getObject
    * 绑定的是getEarlyBeanReference方法,注释会讲-> aab:populateBean填充bean的属性.首先完成xml属性注入,然后调用SmartInstantiationAwareBeanPostProcessor,
    * 调用每个后置处理器的postProcessProperties方法,其中调用完AutowiredAnnotationBeanPostProcessor
    * 的postProcessProperties后完成Autowired注解注入.而一些后置处理器,比如AOP后置处理器,这个postProcessProperties
    * 的实现是直接把参数原封不动返回了,即不做任何操作
    * populateBean注入完属性后,就此完成bean的实例化->aab:initializeBean->调用每个后置处理器的postProcessBeforeInitialization
    * ->aab:invokeInitMethods(调用初始方法,不是构造方法而是自己定义的初始方法)->调用每个后置处理器的postProcessAfterInitialization*/

    //getBean，容器的核心方法
    public Object getBean(String beanName) throws BeansException {
        //先尝试从缓存中直接拿bean实例
        //TODO 在这加上对FactoryBean的识别逻辑
        Object singleton = this.getSingleton(beanName);
        //如果此时还没有这个bean的实例，则获取它的定义来创建实例
        if (singleton == null) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition == null) {
                if(this.getParentBeanFactory()==null){
                    throw new BeansException("No bean named '" + beanName + "' is defined");
                }else return null;
            }
            singleton= this.getSingleton(beanName,()->{
                return this.createBean(beanDefinition);
            });
        }
        if (singleton instanceof FactoryBean) {
            return this.getObjectForBeanInstance(singleton, beanName);
        }
        if(singleton!=null){
            System.out.println("当前bean:"+beanName+",是否是代理类:"+Proxy.isProxyClass(singleton.getClass()));
        }
        return singleton;
    }
    /*TODO:这里代理方法的时序调用与Spring存在显著差异,AOP这块太复杂了,
     * 已知的问题有getObjectForBeanInstance产生早早期代理,然后ostProcessBeforeInstantiation
     * 产生早期代理,然后ostProcessAfterInstantiation完成最终代理,源码看麻了
     */
    protected abstract Object createBean(BeanDefinition beanDefinition) ;

    protected void populateBean(BeanDefinition beanDefinition, Class<?> clz, Object obj){
        //执行xml注入
        handleProperties(beanDefinition, clz, obj);
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
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }


    @Override
    public boolean containsBean(String name) {
        return beanDefinitionMap.containsKey(name);
    }
    public String[] getBeanNamesByType(Class<?> type) throws BeansException {
        //log.info("getBeanNamesByType:{}",type);
        //检查是否在缓存中
        String[] beanNames = allBeanNamesByType.get(type);
        if(beanNames!=null){
            return beanNames;
        }
        beanNames = doGetBeanNamesByType(type);
        if(beanNames.length!=0){
            //把结果加入缓存
            allBeanNamesByType.put(type, beanNames);
            return beanNames;
        }
        /*TODO:Spring的该方法是不会向父容器查找的,但是resolveDependency会向父容器查找,具体调用链比较复杂
            日后再完善 */
        if (parentBeanFactory != null) {
            return ((AbstractBeanFactory)parentBeanFactory).getBeanNamesByType(type);
        }else throw new BeansException("对应类型的Bean不存在,类型:"+type);
    }
    //扫描所有的Bean,返回对应type(
    private String[] doGetBeanNamesByType(Class<?> type) {
        //TODO:完成对FactoryBean的检查
        //log.info("doGetBeanNamesByType:{}",type);
        List<String> beanNames = new ArrayList<>();
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition bd = beanDefinitionMap.get(beanName);
            Class<?> beanClass=bd.getBeanClass();
            if (beanClass == type||type.isAssignableFrom(beanClass)) {
                beanNames.add(beanName);
            }
        }
        return beanNames.toArray(new String[0]);
    }
    //TODO 可能要加上对FactoryBean的识别逻辑
    public void preInstantiateSingletons(){
        for (BeanDefinition bd : nonLazyInitBeans) {
            try {
                getBean(bd.getId());
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(name, beanDefinition);
        this.beanDefinitionNames.add(name);
        if (!beanDefinition.isLazyInit()) {
            nonLazyInitBeans.add(beanDefinition);
        }
    }
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }

    public List<BeanDefinition> getNonLazyInitBeans() {
        return nonLazyInitBeans;
    }

    public void registerBean(String name, Object obj) {
        this.registerSingleton(name, obj);
    }

    public BeanFactory getParentBeanFactory() {
        return parentBeanFactory;
    }

    public void setParentBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    protected Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        // Now we have the bean instance, which may be a normal bean or a FactoryBean.
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }

        Object object = null;
        FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
        object = getObjectFromFactoryBean(factory, beanName);
        return object;
    }



}
