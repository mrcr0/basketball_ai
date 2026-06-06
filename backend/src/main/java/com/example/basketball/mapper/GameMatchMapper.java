package com.example.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.GameMatch;
import org.apache.ibatis.annotations.Mapper;

/**
 * 比赛Mapper
 */
@Mapper
public interface GameMatchMapper extends BaseMapper<GameMatch> {
}