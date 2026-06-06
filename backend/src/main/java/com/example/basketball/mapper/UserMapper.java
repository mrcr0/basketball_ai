package com.example.basketball.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM app_user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM app_user WHERE email = #{email}")
    User selectByEmail(String email);

    @Select("SELECT * FROM app_user")
    java.util.List<User> selectAll();

    @Select("SELECT COUNT(*) FROM app_user")
    long selectCount();

    @Select("SELECT COUNT(*) FROM app_user WHERE status = 1")
    long selectActiveCount();
}
