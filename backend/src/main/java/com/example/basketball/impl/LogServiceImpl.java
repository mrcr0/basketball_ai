package com.example.basketball.impl;

import com.example.basketball.entity.LoginLog;
import com.example.basketball.entity.OperationLog;
import com.example.basketball.mapper.LoginLogMapper;
import com.example.basketball.mapper.OperationLogMapper;
import com.example.basketball.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    @Async
    public void recordLoginLog(Long userId, String username, String loginIp, String userAgent, String status, String errorMessage) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setLoginIp(loginIp);
        log.setUserAgent(userAgent);
        log.setLoginStatus(status);
        log.setErrorMessage(errorMessage);
        log.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(log);
    }

    @Override
    @Async
    public void recordOperationLog(Long userId, String username, String operationType, String module, String desc,
                                   String requestUrl, String requestMethod, String requestParams,
                                   Integer responseCode, String responseMessage, String operationIp) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(operationType);
        log.setOperationModule(module);
        log.setOperationDesc(desc);
        log.setRequestUrl(requestUrl);
        log.setRequestMethod(requestMethod);
        log.setRequestParams(requestParams);
        log.setResponseCode(responseCode);
        log.setResponseMessage(responseMessage);
        log.setOperationIp(operationIp);
        log.setOperationTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}