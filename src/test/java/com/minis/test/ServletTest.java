package com.minis.test;

import com.minis.web.DispatcherServlet;

public class ServletTest {
    public static void main(String[] args) {
        DispatcherServlet ds = new DispatcherServlet();
        try {
            System.out.println(Class.forName("com.minis.test.controller.ServletTest"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public String print(){
        return "Rustie";
    }
}
