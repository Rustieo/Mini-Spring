package com.minis.web;

import com.minis.web.bind.WebDataBinder;

public interface WebBindingInitializer {
    void initBinder(WebDataBinder binder);
}