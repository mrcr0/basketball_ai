package com.example.basketball.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.Dynamic;

@Mapper
public interface DynamicMapper extends BaseMapper<Dynamic> {

    @Select("SELECT * FROM dynamic WHERE is_approved = 1 ORDER BY created_at DESC")
    java.util.List<Dynamic> selectApproved();

    // 暂时返回所有已审核的动态作为赛事讨论（兼容无 type 列的情况）
    @Select("SELECT * FROM dynamic WHERE is_approved = 1 ORDER BY created_at DESC LIMIT 20")
    java.util.List<Dynamic> selectMatchDiscussions();
}
