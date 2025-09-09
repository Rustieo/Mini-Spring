package com.minis.aop;
//NOTE 这个类是我自己瞎写的
public class DefaultClassFilter implements ClassFilter {
    private String pattern;
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.getName().matches(pattern);
    }
}
