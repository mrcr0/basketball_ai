package com.example.basketball.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.entity.DataBackup;
import com.example.basketball.entity.Dynamic;
import com.example.basketball.entity.LoginLog;
import com.example.basketball.entity.OperationLog;
import com.example.basketball.entity.User;
import com.example.basketball.mapper.DynamicMapper;
import com.example.basketball.mapper.LoginLogMapper;
import com.example.basketball.mapper.OperationLogMapper;
import com.example.basketball.mapper.UserMapper;
import com.example.basketball.service.DataBackupService;
import com.example.basketball.service.DynamicService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserMapper userMapper;
    private final DynamicMapper dynamicMapper;
    private final LoginLogMapper loginLogMapper;
    private final OperationLogMapper operationLogMapper;
    private final DynamicService dynamicService;
    private final DataBackupService dataBackupService;

    public AdminController(UserMapper userMapper, DynamicMapper dynamicMapper, 
                          LoginLogMapper loginLogMapper, OperationLogMapper operationLogMapper,
                          DynamicService dynamicService, DataBackupService dataBackupService) {
        this.userMapper = userMapper;
        this.dynamicMapper = dynamicMapper;
        this.loginLogMapper = loginLogMapper;
        this.operationLogMapper = operationLogMapper;
        this.dynamicService = dynamicService;
        this.dataBackupService = dataBackupService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return ResponseEntity.ok(ApiResponse.success("状态更新成功", null));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setRole(role);
        userMapper.updateById(user);
        return ResponseEntity.ok(ApiResponse.success("角色更新成功", null));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.deleteById(userId);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @GetMapping("/dynamics/pending")
    public ResponseEntity<ApiResponse<List<Dynamic>>> getPendingDynamics() {
        List<Dynamic> dynamics = dynamicMapper.selectByMap(Map.of("is_approved", 0));
        return ResponseEntity.ok(ApiResponse.success(dynamics));
    }

    @PutMapping("/dynamics/{dynamicId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveDynamic(@PathVariable Long dynamicId) {
        dynamicService.approveDynamic(dynamicId);
        return ResponseEntity.ok(ApiResponse.success("审核通过", null));
    }

    @DeleteMapping("/dynamics/{dynamicId}")
    public ResponseEntity<ApiResponse<Void>> deleteDynamic(@PathVariable Long dynamicId) {
        dynamicService.deleteDynamic(dynamicId);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @GetMapping("/login-logs")
    public ResponseEntity<ApiResponse<List<LoginLog>>> getLoginLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {
        QueryWrapper<LoginLog> query = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            query.like("username", username);
        }
        if (status != null && !status.isEmpty()) {
            query.eq("login_status", status);
        }
        query.orderByDesc("login_time");
        List<LoginLog> logs = loginLogMapper.selectList(query);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/operation-logs")
    public ResponseEntity<ApiResponse<List<OperationLog>>> getOperationLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module) {
        QueryWrapper<OperationLog> query = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            query.like("username", username);
        }
        if (module != null && !module.isEmpty()) {
            query.eq("operation_module", module);
        }
        query.orderByDesc("operation_time");
        List<OperationLog> logs = operationLogMapper.selectList(query);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @GetMapping("/backups")
    public ResponseEntity<ApiResponse<List<DataBackup>>> getBackups() {
        List<DataBackup> backups = dataBackupService.listBackups();
        return ResponseEntity.ok(ApiResponse.success(backups));
    }

    @PostMapping("/backups/create")
    public ResponseEntity<ApiResponse<DataBackup>> createBackup() {
        DataBackup backup = dataBackupService.createBackup("full");
        return ResponseEntity.ok(ApiResponse.success("备份成功", backup));
    }

    @PostMapping("/backups/{id}/restore")
    public ResponseEntity<ApiResponse<String>> restoreBackup(@PathVariable Long id) {
        dataBackupService.restoreBackup(id);
        return ResponseEntity.ok(ApiResponse.success("恢复成功"));
    }

    @DeleteMapping("/backups/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBackup(@PathVariable Long id) {
        dataBackupService.deleteBackup(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        long userCount = userMapper.selectCount(null);
        long dynamicCount = dynamicMapper.selectCount(null);
        long pendingCount = dynamicMapper.selectCount(new QueryWrapper<Dynamic>().eq("is_approved", 0));
        long loginSuccessCount = loginLogMapper.selectCount(new QueryWrapper<LoginLog>().eq("login_status", "SUCCESS"));
        long backupCount = dataBackupService.listBackups().size();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("userCount", userCount);
        dashboard.put("activeUsers", userMapper.selectCount(new QueryWrapper<User>().eq("status", 1)));
        dashboard.put("dynamicCount", dynamicCount);
        dashboard.put("pendingCount", pendingCount);
        dashboard.put("loginSuccessCount", loginSuccessCount);
        dashboard.put("backupCount", backupCount);

        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("activeUsers", userMapper.selectCount(new QueryWrapper<User>().eq("status", 1)));
        stats.put("adminUsers", userMapper.selectCount(new QueryWrapper<User>().eq("role", "admin")));
        
        // 动态统计
        stats.put("totalDynamics", dynamicMapper.selectCount(null));
        stats.put("pendingDynamics", dynamicMapper.selectCount(new QueryWrapper<Dynamic>().eq("is_approved", 0)));
        
        // 日志统计
        stats.put("totalLoginAttempts", loginLogMapper.selectCount(null));
        stats.put("successfulLogins", loginLogMapper.selectCount(new QueryWrapper<LoginLog>().eq("login_status", "SUCCESS")));
        
        // 备份统计
        stats.put("backupCount", dataBackupService.listBackups().size());

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

}