package com.example.basketball.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.basketball.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}