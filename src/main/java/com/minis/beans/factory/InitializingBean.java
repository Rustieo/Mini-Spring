package com.minis.beans.factory;

public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
