package com.minis.test;

import com.minis.beans.factory.ClassPathXmlResource;

public class Test1 {
    public static void main(String[] args) {
        System.out.println(ClassPathXmlResource.class.getClassLoader().getResource("testBean.xml"));
    }
}
