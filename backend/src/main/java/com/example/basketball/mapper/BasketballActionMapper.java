package com.example.basketball.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.BasketballAction;

@Mapper
public interface BasketballActionMapper extends BaseMapper<BasketballAction> {

    @Select("SELECT * FROM basketball_action WHERE category = #{category} AND is_published = 1")
    java.util.List<BasketballAction> selectByCategory(String category);

    @Select("SELECT * FROM basketball_action WHERE difficulty = #{difficulty} AND is_published = 1")
    java.util.List<BasketballAction> selectByDifficulty(String difficulty);
}
