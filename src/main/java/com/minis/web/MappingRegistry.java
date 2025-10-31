package com.minis.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingRegistry {
    //储存被映射的URL
    //注:这里的URL不包含localhost:8080
    private List<String>        urlMappingNames = new ArrayList<>();
    //储存URL:对象(Controller)
    private Map<String,Object>  mappingObjs = new HashMap<>();
    //储存URL:方法,然后配合上面的实现method.invoke(obj)
    private Map<String,Method>  mappingMethods = new HashMap<>();
    public List<String> getUrlMappingNames() {
        return urlMappingNames;
    }
    public void setUrlMappingNames(List<String> urlMappingNames) {
        this.urlMappingNames = urlMappingNames;
    }
    public Map<String,Object> getMappingObjs() {
        return mappingObjs;
    }
    public void setMappingObjs(Map<String,Object> mappingObjs) {
        this.mappingObjs = mappingObjs;
    }
    public Map<String,Method> getMappingMethods() {
        return mappingMethods;
    }
    public void setMappingMethods(Map<String, Method> mappingMethods) {
        this.mappingMethods = mappingMethods;
    }
}