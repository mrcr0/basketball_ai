package com.example.basketball.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.TacticInfo;

@Mapper
public interface TacticInfoMapper extends BaseMapper<TacticInfo> {

    @Select("SELECT * FROM tactic_info WHERE type = #{type} AND is_published = 1 ORDER BY created_at DESC")
    java.util.List<TacticInfo> selectByType(String type);
}
