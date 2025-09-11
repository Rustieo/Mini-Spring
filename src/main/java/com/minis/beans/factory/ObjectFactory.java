package com.minis.beans.factory;


import com.minis.beans.BeansException;

@FunctionalInterface
public interface ObjectFactory <T> {
    T getObject() throws BeansException;
}
