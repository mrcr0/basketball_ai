package com.example.basketball.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.TrainRecord;

@Mapper
public interface TrainRecordMapper extends BaseMapper<TrainRecord> {

    @Select("SELECT * FROM train_record WHERE user_id = #{userId} ORDER BY check_in_time DESC")
    java.util.List<TrainRecord> selectByUserId(Long userId);

    @Select("SELECT * FROM train_record WHERE user_id = #{userId} AND check_in_time BETWEEN #{startTime} AND #{endTime} ORDER BY check_in_time DESC")
    java.util.List<TrainRecord> selectByUserIdAndDateRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
