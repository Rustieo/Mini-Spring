package com.minis.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.test.AServiceImpl;
import com.minis.web.servlet.DispatcherServlet;
import com.minis.web.bind.annotation.RequestMapping;

public class ServletTest {
    public static void main(String[] args) {
        DispatcherServlet ds = new DispatcherServlet();
        try {
            System.out.println(Class.forName("com.minis.test.controller.ServletTest"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Autowired
    AServiceImpl aService;
    @RequestMapping("/testA")
    public String testA(){
        return aService.methodA();
    }
    @RequestMapping("/helloworld")
    public String print(){
        return "Rustie";
    }
}