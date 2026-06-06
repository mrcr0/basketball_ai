package com.example.basketball.service;

import com.example.basketball.entity.LoginLog;
import com.example.basketball.entity.OperationLog;

public interface LogService {

    void recordLoginLog(Long userId, String username, String loginIp, String userAgent, String status, String errorMessage);

    void recordOperationLog(Long userId, String username, String operationType, String module, String desc,
                            String requestUrl, String requestMethod, String requestParams,
                            Integer responseCode, String responseMessage, String operationIp);
}