package com.minis.test;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.Autowired;
import com.minis.context.ClassPathXmlApplicationContext;

public class BaseService {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = null;
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        BaseService bs=null;
        try {
            bs = (BaseService) ctx.getBean("baseService");
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        bs.callAs();
    }
    @Autowired
    private BaseBaseService bbs;
    @Autowired
    private AService aService;

    public void callAs() {
        aService.sayHello();
    }

    public BaseService() {
    }
    public void sayHello() {
        System.out.println("Base Service says Hello");
        bbs.getAs().sayHello();
    }
    public BaseBaseService getBbs() {
        return bbs;
    }
    public void printBaseService() {
        System.out.println("BaseService: ");
    }
    public void setBbs(BaseBaseService bbs) {
        this.bbs = bbs;
    }
}
