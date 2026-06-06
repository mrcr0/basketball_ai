package com.example.basketball.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.SportsNews;

@Mapper
public interface SportsNewsMapper extends BaseMapper<SportsNews> {

    @Select("SELECT * FROM sports_news WHERE news_type = #{newsType} AND is_published = 1 ORDER BY publish_time DESC LIMIT #{limit}")
    List<SportsNews> selectByType(String newsType, int limit);

    @Select("SELECT * FROM sports_news WHERE league = #{league} AND is_published = 1 ORDER BY publish_time DESC LIMIT #{limit}")
    List<SportsNews> selectByLeague(String league, int limit);

    @Select("SELECT * FROM sports_news WHERE is_published = 1 ORDER BY publish_time DESC LIMIT #{limit}")
    List<SportsNews> selectLatest(int limit);

    @Select("SELECT * FROM sports_news WHERE is_verified = 0 AND is_published = 0 ORDER BY created_at DESC")
    List<SportsNews> selectPendingReview();

    @Select("SELECT * FROM sports_news WHERE source = #{source} AND is_published = 1 ORDER BY publish_time DESC LIMIT #{limit}")
    List<SportsNews> selectBySource(String source, int limit);
}
