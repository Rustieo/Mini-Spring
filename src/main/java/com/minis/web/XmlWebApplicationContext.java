package com.minis.web;

import com.minis.context.ClassPathXmlApplicationContext;
import jakarta.servlet.ServletContext;

//根上下文
public class XmlWebApplicationContext
        extends ClassPathXmlApplicationContext implements WebApplicationContext{
    private ServletContext servletContext;

    public XmlWebApplicationContext(String fileName) {
        super(fileName);
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
