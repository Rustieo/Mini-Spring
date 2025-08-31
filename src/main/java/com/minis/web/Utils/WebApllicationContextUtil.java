package com.minis.web.Utils;

import com.minis.web.WebApplicationContext;
import jakarta.servlet.ServletContext;

public class WebApllicationContextUtil {

    //根据ServletContext和属性名获取WebApplicationContext
    public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
        Object attr = sc.getAttribute(attrName);
        if (attr == null) {
            return null;
        } else if (attr instanceof RuntimeException) {
            throw (RuntimeException)attr;
        } else if (attr instanceof Error) {
            throw (Error)attr;
        } else if (attr instanceof Exception) {
            throw new IllegalStateException((Exception)attr);
        } else if (!(attr instanceof WebApplicationContext)) {
            throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
        } else {
            return (WebApplicationContext)attr;
        }
    }
}
