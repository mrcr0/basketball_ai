package com.example.basketball.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.BasketballPlan;

@Mapper
public interface BasketballPlanMapper extends BaseMapper<BasketballPlan> {

    @Select("SELECT * FROM basketball_plan WHERE user_id = #{userId} ORDER BY created_at DESC")
    java.util.List<BasketballPlan> selectByUserId(Long userId);
}
