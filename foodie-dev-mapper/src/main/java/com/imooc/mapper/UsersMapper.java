package com.imooc.mapper;

import com.imooc.my.mapper.MyMapper;
import com.imooc.pojo.Users;

public interface UsersMapper extends MyMapper<Users> {
    Users login(String username, String password);
}