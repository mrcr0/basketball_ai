package com.example.basketball.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.BasketballKnowledge;

@Mapper
public interface BasketballKnowledgeMapper extends BaseMapper<BasketballKnowledge> {

    @Select("SELECT * FROM basketball_knowledge WHERE category = #{category} AND is_published = 1 ORDER BY created_at DESC")
    List<BasketballKnowledge> selectByCategory(String category);

    @Select("SELECT * FROM basketball_knowledge WHERE is_published = 1 ORDER BY view_count DESC LIMIT #{limit}")
    List<BasketballKnowledge> selectHotList(int limit);

    @Select("SELECT DISTINCT sub_category FROM basketball_knowledge WHERE category = #{category} AND is_published = 1")
    List<String> selectSubCategories(String category);
}
