package com.minis.test.mapper;

import com.minis.test.entity.User;
import java.util.List;

public interface UserMapper {
    User getUserInfo(Integer id);
    // pagination: limit size offset offset
    List<User> listPage(Integer size, Integer offset);
    Integer countAll();
}
