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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.basketball.client.TencentSportsApiClient;
import com.example.basketball.config.TencentSportsProperties;
import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.entity.SportsNews;
import com.example.basketball.service.ApiCacheManager;
import com.example.basketball.service.SportsNewsService;

@RestController
@RequestMapping("/api/news")
public class SportsNewsController {

    private final SportsNewsService newsService;
    private final TencentSportsApiClient tencentApiClient;
    private final TencentSportsProperties tencentProps;
    private final ApiCacheManager cacheManager;

    public SportsNewsController(SportsNewsService newsService, TencentSportsApiClient tencentApiClient,
                                 TencentSportsProperties tencentProps, ApiCacheManager cacheManager) {
        this.newsService = newsService;
        this.tencentApiClient = tencentApiClient;
        this.tencentProps = tencentProps;
        this.cacheManager = cacheManager;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SportsNews>>> getAllNews(
            @RequestParam(required = false) String newsType,
            @RequestParam(required = false) String league,
            @RequestParam(defaultValue = "20") int limit) {
        List<SportsNews> news;
        if (newsType != null && !newsType.isEmpty()) {
            news = newsService.getNewsByType(newsType, limit);
        } else if (league != null && !league.isEmpty()) {
            news = newsService.getNewsByLeague(league, limit);
        } else {
            news = newsService.getLatestNews(limit);
        }
        return ResponseEntity.ok(ApiResponse.success(news));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<SportsNews>>> getLatestNews(
            @RequestParam(defaultValue = "10") int limit) {
        List<SportsNews> latest = newsService.getLatestNews(limit);
        return ResponseEntity.ok(ApiResponse.success(latest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SportsNews>> getNews(@PathVariable Long id) {
        SportsNews news = newsService.getNewsById(id);
        return ResponseEntity.ok(ApiResponse.success(news));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SportsNews>> createNews(@RequestBody SportsNews news) {
        SportsNews created = newsService.createNews(news);
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SportsNews>> updateNews(
            @PathVariable Long id, @RequestBody SportsNews news) {
        SportsNews updated = newsService.updateNews(id, news);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> triggerRefresh() {
        newsService.scheduledNewsRefresh();
        return ResponseEntity.ok(ApiResponse.success("资讯刷新任务已触发"));
    }

    /**
     * 腾讯体育API健康检查与状态监控
     */
    @GetMapping("/api-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApiStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", tencentProps.isEnabled());
        status.put("healthy", tencentApiClient.isHealthy());
        status.put("baseUrl", tencentProps.getBaseUrl());
        status.put("cacheSize", cacheManager.size());
        status.put("stats", tencentApiClient.getStats());
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    /**
     * 清除API缓存（用于调试或强制刷新）
     */
    @PostMapping("/cache-clear")
    public ResponseEntity<ApiResponse<String>> clearCache() {
        cacheManager.invalidateAll();
        return ResponseEntity.ok(ApiResponse.success("API缓存已清除"));
    }

    /**
     * 获取腾讯体育NBA热门赛事资讯（优先展示）
     */
    @GetMapping("/tencent")
    public ResponseEntity<ApiResponse<List<SportsNews>>> getTencentNews() {
        List<SportsNews> tencentNews = newsService.getTencentNews();
        return ResponseEntity.ok(ApiResponse.success(tencentNews));
    }
}
