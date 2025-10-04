package com.minis.test.service;

import com.minis.context.ClassPathXmlApplicationContext;
import com.minis.test.entity.User;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserServiceIT {
    @Test
    @Ignore("需要本地 SQL Server(MSSQL) DEMO 数据库以及 users 表；就绪后去掉 @Ignore 运行")
    public void getUserById_shouldReturnUser() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserService userService = (UserService) ctx.getBean("userService");
        User u = userService.getUserById(1);
        assertNotNull(u);
        System.out.println(u.getId() + ", " + u.getName() + ", " + u.getBirthday());
    }
}

