package com.minis.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.test.service.IAction;
import com.minis.web.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class HelloWorldBean {
    @Autowired
    IAction action;

    @RequestMapping("/testaop")
    public void doTestAop(HttpServletRequest request, HttpServletResponse response) {
        action.doAction();

        String str = "test aop, hello world!";
        try {
            response.getWriter().write(str);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}