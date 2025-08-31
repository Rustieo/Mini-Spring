package com.minis.web;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

public class RequestMappingHandlerMapping implements HandlerMapping{
    WebApplicationContext wac;
    private final MappingRegistry mappingRegistry = new MappingRegistry();
    public RequestMappingHandlerMapping(WebApplicationContext wac) {
        this.wac = wac;
        initMapping();
    }
    //建立URL与调用方法和实例的映射关系，存储在mappingRegistry中
    protected void initMapping() {
        Class<?> clz = null;
        Object obj = null;
        String[] controllerNames = this.wac.getBeanDefinitionNames();
        //扫描WAC中存放的所有bean
        for (String controllerName : controllerNames) {
            try {
                clz = Class.forName(controllerName);
                obj = this.wac.getBean(controllerName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Method[] methods = clz.getDeclaredMethods();
            if(methods!=null){
                for(Method method: methods){
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        String methodName=method.getName();
                        String URL=method.getAnnotation(RequestMapping.class).value();
                        this.mappingRegistry.getUrlMappingNames().add(URL);
                        this.mappingRegistry.getMappingObjs().put(URL,obj);
                        this.mappingRegistry.getMappingMethods().put(URL,method);
                    }
                }
            }
        }
    }

    //根据访问URL查找对应的调用方法
    public HandlerMethod getHandler(HttpServletRequest request) throws Exception
    {
        String URL=request.getServletPath();
        if(this.mappingRegistry.getUrlMappingNames().contains(URL)){
            Object obj=this.mappingRegistry.getMappingObjs().get(URL);
            Method method=this.mappingRegistry.getMappingMethods().get(URL);
            return new HandlerMethod(method,obj);
        }
        return null;
    }
}