package com.example.basketball.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.basketball.dto.request.CommentRequest;
import com.example.basketball.dto.request.DynamicRequest;
import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.dto.response.DynamicDetailResponse;
import com.example.basketball.entity.Dynamic;
import com.example.basketball.service.DynamicService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dynamics")
public class DynamicController {

    private static final Long ANONYMOUS_USER_ID = 0L;

    private final DynamicService dynamicService;

    public DynamicController(DynamicService dynamicService) {
        this.dynamicService = dynamicService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Dynamic>> createDynamic(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody DynamicRequest request) {
        Long userId = extractUserId(token);
        Dynamic dynamic = dynamicService.createDynamic(userId, request);
        return ResponseEntity.ok(ApiResponse.success("发布成功", dynamic));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DynamicDetailResponse>>> getAllDynamics() {
        // 只返回赛事讨论类型(type=match)的动态
        List<DynamicDetailResponse> dynamics = dynamicService.getMatchDiscussionsWithDetails();
        return ResponseEntity.ok(ApiResponse.success(dynamics));
    }

    @GetMapping("/{dynamicId}")
    public ResponseEntity<ApiResponse<DynamicDetailResponse>> getDynamicDetail(@PathVariable Long dynamicId) {
        DynamicDetailResponse detail = dynamicService.getDynamicDetail(dynamicId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @PostMapping("/{dynamicId}/like")
    public ResponseEntity<ApiResponse<Integer>> likeDynamic(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long dynamicId) {
        Long userId = extractUserId(token);
        Integer likeCount = dynamicService.toggleLike(dynamicId, userId);
        return ResponseEntity.ok(ApiResponse.success("操作成功", likeCount));
    }

    @PostMapping("/{dynamicId}/comments")
    public ResponseEntity<ApiResponse<Void>> addComment(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long dynamicId,
            @Valid @RequestBody CommentRequest request) {
        Long userId = extractUserId(token);
        dynamicService.addComment(dynamicId, userId, request.getContent(), request.getParentId());
        return ResponseEntity.ok(ApiResponse.success("评论成功", null));
    }

    @PostMapping("/{dynamicId}/share")
    public ResponseEntity<ApiResponse<String>> shareDynamic(@PathVariable Long dynamicId) {
        String shareUrl = "http://localhost:5173/community?dynamicId=" + dynamicId;
        return ResponseEntity.ok(ApiResponse.success("分享链接生成成功", shareUrl));
    }

    @PostMapping("/comments/{commentId}/update")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {
        Long userId = extractUserId(token);
        dynamicService.updateComment(commentId, userId, request.getContent());
        return ResponseEntity.ok(ApiResponse.success("评论更新成功", null));
    }

    @PostMapping("/comments/{commentId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long commentId) {
        Long userId = extractUserId(token);
        dynamicService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success("评论删除成功", null));
    }

    private Long extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return 1L;
        }
        return ANONYMOUS_USER_ID;
    }

}
