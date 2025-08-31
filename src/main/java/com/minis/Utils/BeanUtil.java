package com.minis.Utils;

import java.beans.Introspector;

public class BeanUtil {
    public static String convertClassName(String className) {
        return Introspector.decapitalize(className);
    }
}
