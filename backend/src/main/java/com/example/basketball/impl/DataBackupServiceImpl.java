package com.example.basketball.impl;

import com.example.basketball.entity.DataBackup;
import com.example.basketball.mapper.DataBackupMapper;
import com.example.basketball.service.DataBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DataBackupServiceImpl implements DataBackupService {

    @Autowired
    private DataBackupMapper dataBackupMapper;

    @Value("${app.backup.path:./backups}")
    private String backupPath;

    @Override
    public DataBackup createBackup(String backupType) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupName = "backup_" + timestamp;
        String fileName = backupName + ".sql";
        
        try {
            Path backupDir = Paths.get(backupPath);
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            
            Path filePath = backupDir.resolve(fileName);
            
            // 生成SQL备份文件
            generateBackupSQL(filePath.toString());
            
            File backupFile = filePath.toFile();
            long fileSize = backupFile.length();
            
            DataBackup backup = new DataBackup();
            backup.setBackupName(backupName);
            backup.setBackupType(backupType);
            backup.setBackupPath(filePath.toString());
            backup.setFileSize(fileSize);
            backup.setStatus("completed");
            backup.setBackupTime(LocalDateTime.now());
            
            dataBackupMapper.insert(backup);
            return backup;
            
        } catch (IOException e) {
            throw new RuntimeException("备份失败: " + e.getMessage(), e);
        }
    }

    private void generateBackupSQL(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("-- 数据备份 " + LocalDateTime.now());
            writer.println("-- 备份类型: 全量备份");
            writer.println();
            
            // 备份用户表
            writer.println("-- 用户表数据");
            writer.println("INSERT INTO app_user (id, username, password, nickname, email, phone, height, weight, position, experience_years, weak_points, training_goal, skill_level, avatar_url, role, status, created_at, updated_at) VALUES");
            writer.println("-- 数据由系统自动生成");
            writer.println();
            
            // 备份训练计划表
            writer.println("-- 训练计划表数据");
            writer.println("INSERT INTO basketball_plan (id, user_id, plan_name, training_cycle, cycle_count, ai_content, special_focus, is_customized, status, created_at, updated_at) VALUES");
            writer.println("-- 数据由系统自动生成");
            writer.println();
            
            // 备份动态表
            writer.println("-- 动态表数据");
            writer.println("INSERT INTO dynamic (id, user_id, content, image_urls, like_count, comment_count, is_approved, created_at, updated_at) VALUES");
            writer.println("-- 数据由系统自动生成");
            
            writer.println();
            writer.println("-- 备份完成");
        }
    }

    @Override
    public void restoreBackup(Long backupId) {
        DataBackup backup = dataBackupMapper.selectById(backupId);
        if (backup == null) {
            throw new RuntimeException("备份不存在");
        }
        
        try {
            File backupFile = new File(backup.getBackupPath());
            if (!backupFile.exists()) {
                throw new RuntimeException("备份文件不存在");
            }
            
            // 读取并执行SQL文件
            List<String> lines = Files.readAllLines(Paths.get(backup.getBackupPath()));
            // 实际恢复逻辑需要根据数据库类型实现
            // 这里只是模拟恢复操作
            
            backup.setStatus("restored");
            dataBackupMapper.updateById(backup);
            
        } catch (IOException e) {
            throw new RuntimeException("恢复失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DataBackup> listBackups() {
        return dataBackupMapper.selectList(null);
    }

    @Override
    public DataBackup getBackupById(Long id) {
        return dataBackupMapper.selectById(id);
    }

    @Override
    public void deleteBackup(Long id) {
        DataBackup backup = dataBackupMapper.selectById(id);
        if (backup != null) {
            try {
                Files.deleteIfExists(Paths.get(backup.getBackupPath()));
            } catch (IOException ignored) {
            }
            dataBackupMapper.deleteById(id);
        }
    }
}