package com.example.basketball.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT * FROM comment WHERE dynamic_id = #{dynamicId} AND is_approved = 1 ORDER BY created_at ASC")
    java.util.List<Comment> selectByDynamicId(Long dynamicId);
}
