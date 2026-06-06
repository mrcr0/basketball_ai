package com.example.basketball.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.entity.DataBackup;
import com.example.basketball.service.DataBackupService;

@RestController
@RequestMapping("/api/backup")
public class DataBackupController {

    private final DataBackupService dataBackupService;

    public DataBackupController(DataBackupService dataBackupService) {
        this.dataBackupService = dataBackupService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DataBackup>> createBackup(@RequestParam(defaultValue = "full") String type) {
        DataBackup backup = dataBackupService.createBackup(type);
        return ResponseEntity.ok(ApiResponse.success("备份成功", backup));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DataBackup>>> listBackups() {
        List<DataBackup> backups = dataBackupService.listBackups();
        return ResponseEntity.ok(ApiResponse.success(backups));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DataBackup>> getBackup(@PathVariable Long id) {
        DataBackup backup = dataBackupService.getBackupById(id);
        if (backup == null) {
            return ResponseEntity.ok(ApiResponse.error("备份不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(backup));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<String>> restoreBackup(@PathVariable Long id) {
        dataBackupService.restoreBackup(id);
        return ResponseEntity.ok(ApiResponse.success("恢复成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBackup(@PathVariable Long id) {
        dataBackupService.deleteBackup(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }
}