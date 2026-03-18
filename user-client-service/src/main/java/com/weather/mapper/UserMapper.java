package com.weather.mapper;

import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    Integer getUid(@Param("username") String username);

    String getEncryptedPassword(@Param("uid") Integer uid);

    Integer insertUser(@Param("username") String username, @Param("password") String password);
}
