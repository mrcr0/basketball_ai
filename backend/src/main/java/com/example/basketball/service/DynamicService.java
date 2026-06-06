package com.example.basketball.service;

import java.util.List;

import com.example.basketball.dto.request.DynamicRequest;
import com.example.basketball.dto.response.DynamicDetailResponse;
import com.example.basketball.entity.Dynamic;

public interface DynamicService {

    Dynamic createDynamic(Long userId, DynamicRequest request);

    List<Dynamic> getAllApprovedDynamics();
    
    List<DynamicDetailResponse> getAllApprovedDynamicsWithDetails();

    List<DynamicDetailResponse> getMatchDiscussionsWithDetails();

    DynamicDetailResponse getDynamicDetail(Long dynamicId);

    Integer toggleLike(Long dynamicId, Long userId);

    void addComment(Long dynamicId, Long userId, String content, Long parentId);

    void updateComment(Long commentId, Long userId, String content);

    void deleteComment(Long commentId, Long userId);

    void deleteDynamic(Long dynamicId);

    void approveDynamic(Long dynamicId);

}
