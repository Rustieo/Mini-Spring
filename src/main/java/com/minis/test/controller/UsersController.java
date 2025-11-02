package com.minis.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.test.entity.PageResult;
import com.minis.test.entity.User;
import com.minis.test.service.UserService;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;

@ResponseBody
public class UsersController {
    @Autowired
    private UserService userService;

    // GET /users/page?page=1&size=10 -> JSON
    @RequestMapping("/users/page")
    public PageResult<User> page(HttpServletRequest request) {
        Integer page = null;
        Integer size = null;
        try { String p = request.getParameter("page"); if (p != null) page = Integer.parseInt(p); } catch (Exception ignored) {}
        try { String s = request.getParameter("size"); if (s != null) size = Integer.parseInt(s); } catch (Exception ignored) {}
        return userService.pageUsers(page, size);
    }
}

