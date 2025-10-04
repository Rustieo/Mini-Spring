package com.minis.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.test.entity.Cat;
import com.minis.test.entity.User;
import com.minis.test.service.IAction;
import com.minis.test.service.UserService;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;

@ResponseBody
public class HelloWorldBean {
    @Autowired
    IAction action;

    @Autowired
    UserService userService;

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
    @RequestMapping("/testuser")
    public User testUser() {
        return userService.getUserById(12312023);
    }


}