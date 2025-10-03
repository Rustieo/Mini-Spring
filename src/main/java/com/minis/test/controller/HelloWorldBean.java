package com.minis.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.test.entity.Cat;
import com.minis.test.service.IAction;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;

@ResponseBody
public class HelloWorldBean {
    @Autowired
    IAction action;

    @RequestMapping("/testaop")
    public String doTestAop() {
        action.doAction();
        return  "test aop, hello world!";

    }
    @RequestMapping("/testcat")
    public Cat testCat() {
        Cat   cat = new Cat("hajimi",100);
        return cat;
    }


}