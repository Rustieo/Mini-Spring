package beans.factory.xml;

import beans.*;
import beans.factory.AbstractApplicationContext;
import beans.factory.ApplicationEvent;
import beans.factory.support.DefaultListableBeanFactory;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    //TODO 这里接口的声明感觉有点随便,可以挑个更合适的
    DefaultListableBeanFactory beanFactory=new DefaultListableBeanFactory();
    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }
    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        Resource resource = new ClassPathXmlResource(fileName);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        if (isRefresh) {
            try {
                this.refresh();
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //TODO
    /*public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor
                                                    postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }*/
    public Object getBean(String beanName) throws BeansException {
        return this.beanFactory.getBean(beanName);
    }


    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return null;
    }


    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }

    public void publishEvent(ApplicationEvent event) {
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {

    }


    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }
    @Override
    public void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory)
    {
        this.beanFactory.addBeanPostProcessor(new
                AutowiredAnnotationBeanPostProcessor());
    }
    public void initApplicationEventPublisher(){

    }

    @Override
    protected void onRefresh() {

    }
    @Override
    protected void registerListeners() {

    }

    @Override
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory.preInstantiateSingletons();
    }

    @Override
    protected void finishRefresh() {

    }

    ;

    public boolean isSingleton(String name) {
        return false;
    }
    public boolean isPrototype(String name) {
        return false;
    }
    public Class<?> getType(String name) {
        return null;
    }
}