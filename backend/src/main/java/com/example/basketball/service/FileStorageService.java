package com.example.basketball.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

/**
 * 文件存储服务
 * 处理上传文件的格式验证、大小校验、安全存储与URL生成
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    // ===== 允许的格式 =====
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp"));
    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList("mp4", "avi", "mov"));

    // ===== 允许的MIME类型 =====
    private static final Set<String> ALLOWED_IMAGE_MIMES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/webp"
    ));
    private static final Set<String> ALLOWED_VIDEO_MIMES = new HashSet<>(Arrays.asList(
            "video/mp4", "video/x-msvideo", "video/quicktime"
    ));

    // ===== 文件大小限制 =====
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;       // 10MB
    private static final long MAX_VIDEO_SIZE = 200 * 1024 * 1024;      // 200MB

    @Value("${app.upload.base-dir:uploads}")
    private String baseDir;

    @Value("${app.upload.base-url:/uploads}")
    private String baseUrl;

    private Path uploadRoot;

    @PostConstruct
    public void init() {
        uploadRoot = Paths.get(baseDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
            log.info("FileStorageService initialized. Upload dir: {}", uploadRoot);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + uploadRoot, e);
        }
    }

    /**
     * 存储上传文件
     * @param file   上传文件
     * @param category 分类（images / videos）
     * @return 存储结果（含访问URL和文件名）
     */
    public StoredFile store(MultipartFile file, String category) {
        // 1. 基本校验
        if (file.isEmpty()) {
            throw new FileUploadException("文件为空");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new FileUploadException("文件名为空");
        }

        String extension = getExtension(originalName).toLowerCase();
        String contentType = file.getContentType();

        // 2. 根据分类校验
        if ("images".equals(category)) {
            validateImage(file, extension, contentType);
        } else if ("videos".equals(category)) {
            validateVideo(file, extension, contentType);
        } else {
            throw new FileUploadException("不支持的文件分类: " + category);
        }

        // 3. 生成唯一文件名并存储
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storedName = UUID.randomUUID().toString() + "." + extension;
        Path categoryDir = uploadRoot.resolve(category).resolve(datePath);

        try {
            Files.createDirectories(categoryDir);
        } catch (IOException e) {
            throw new FileUploadException("无法创建存储目录");
        }

        Path targetPath = categoryDir.resolve(storedName);

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store file: {}", targetPath, e);
            throw new FileUploadException("文件存储失败");
        }

        // 4. 生成访问URL
        String accessUrl = baseUrl + "/" + category + "/" + datePath + "/" + storedName;

        log.info("File stored successfully: {} -> {} ({} bytes)", originalName, accessUrl, file.getSize());

        return new StoredFile(originalName, storedName, accessUrl, extension, file.getSize());
    }

    /**
     * 批量存储文件
     */
    public java.util.List<StoredFile> storeBatch(java.util.List<MultipartFile> files, String category) {
        java.util.List<StoredFile> results = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                results.add(store(file, category));
            }
        }
        return results;
    }

    // ===== 校验方法 =====

    private void validateImage(MultipartFile file, String extension, String mimeType) {
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new FileUploadException("不支持的图片格式: ." + extension + "（仅允许 JPG/PNG/WEBP）");
        }
        if (mimeType != null && !ALLOWED_IMAGE_MIMES.contains(mimeType.toLowerCase())) {
            throw new FileUploadException("不支持的图片MIME类型: " + mimeType);
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new FileUploadException("图片文件过大（最大10MB，当前 " + formatSize(file.getSize()) + "）");
        }
    }

    private void validateVideo(MultipartFile file, String extension, String mimeType) {
        if (!ALLOWED_VIDEO_EXTENSIONS.contains(extension)) {
            throw new FileUploadException("不支持的视频格式: ." + extension + "（仅允许 MP4/AVI/MOV）");
        }
        if (mimeType != null && !ALLOWED_VIDEO_MIMES.contains(mimeType.toLowerCase())) {
            throw new FileUploadException("不支持的视频MIME类型: " + mimeType);
        }
        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new FileUploadException("视频文件过大（最大200MB，当前 " + formatSize(file.getSize()) + "）");
        }
    }

    // ===== 工具方法 =====

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot + 1) : "";
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1048576) return String.format("%.1fKB", bytes / 1024.0);
        return String.format("%.1fMB", bytes / 1048576.0);
    }

    // ===== 内部类 =====

    /**
     * 文件存储结果
     */
    public static class StoredFile {
        private final String originalName;
        private final String storedName;
        private final String accessUrl;
        private final String extension;
        private final long size;

        public StoredFile(String originalName, String storedName, String accessUrl, String extension, long size) {
            this.originalName = originalName;
            this.storedName = storedName;
            this.accessUrl = accessUrl;
            this.extension = extension;
            this.size = size;
        }

        public String getOriginalName() { return originalName; }
        public String getStoredName() { return storedName; }
        public String getAccessUrl() { return accessUrl; }
        public String getExtension() { return extension; }
        public long getSize() { return size; }
    }

    /**
     * 文件上传异常
     */
    public static class FileUploadException extends RuntimeException {
        public FileUploadException(String message) {
            super(message);
        }
    }
}
