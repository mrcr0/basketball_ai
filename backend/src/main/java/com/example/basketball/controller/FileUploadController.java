package com.example.basketball.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.service.FileStorageService;
import com.example.basketball.service.FileStorageService.StoredFile;

/**
 * 文件上传控制器
 * 支持图片（JPG/PNG/WEBP ≤10MB）和视频（MP4/AVI/MOV ≤200MB）上传
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final FileStorageService storageService;

    public FileUploadController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 上传图片（支持多文件）
     */
    @PostMapping("/images")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> uploadImages(
            @RequestParam("files") List<MultipartFile> files) {
        return handleUpload(files, "images");
    }

    /**
     * 上传视频（支持多文件）
     */
    @PostMapping("/videos")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> uploadVideos(
            @RequestParam("files") List<MultipartFile> files) {
        return handleUpload(files, "videos");
    }

    /**
     * 统一上传处理
     */
    private ResponseEntity<ApiResponse<List<Map<String, Object>>>> handleUpload(
            List<MultipartFile> files, String category) {

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("请选择要上传的文件"));
        }

        List<Map<String, Object>> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            try {
                StoredFile stored = storageService.store(file, category);
                Map<String, Object> item = new HashMap<>();
                item.put("originalName", stored.getOriginalName());
                item.put("accessUrl", stored.getAccessUrl());
                item.put("extension", stored.getExtension());
                item.put("size", stored.getSize());
                item.put("success", true);
                results.add(item);
            } catch (FileStorageService.FileUploadException e) {
                log.warn("Upload rejected: {} - {}", file.getOriginalFilename(), e.getMessage());
                Map<String, Object> item = new HashMap<>();
                item.put("originalName", file.getOriginalFilename());
                item.put("success", false);
                item.put("error", e.getMessage());
                results.add(item);
                errors.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        // 部分成功时返回200+各文件状态
        if (!results.isEmpty()) {
            String msg = errors.isEmpty() ? "全部上传成功" : "部分文件上传成功";
            return ResponseEntity.ok(ApiResponse.success(msg, results));
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("所有文件上传失败"));
    }
}
