package com.minis.test.service;

public class Action1 implements IAction {
    @Override
    public void doesAction() {
        System.out.println("doesAction");
    }

    @Override
    public void doAction() {
        System.out.println("doAction");
    }
}