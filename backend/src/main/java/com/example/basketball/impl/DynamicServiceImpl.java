package com.example.basketball.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.example.basketball.dto.request.DynamicRequest;
import com.example.basketball.dto.response.DynamicDetailResponse;
import com.example.basketball.entity.Comment;
import com.example.basketball.entity.Dynamic;
import com.example.basketball.entity.User;
import com.example.basketball.mapper.CommentMapper;
import com.example.basketball.mapper.DynamicMapper;
import com.example.basketball.mapper.UserMapper;
import com.example.basketball.service.DynamicService;

@Service
public class DynamicServiceImpl implements DynamicService {

    private static final Logger log = LoggerFactory.getLogger(DynamicServiceImpl.class);
    private static final Long ANONYMOUS_USER_ID = 0L;

    private final DynamicMapper dynamicMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    public DynamicServiceImpl(DynamicMapper dynamicMapper, CommentMapper commentMapper, UserMapper userMapper) {
        this.dynamicMapper = dynamicMapper;
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Dynamic createDynamic(Long userId, DynamicRequest request) {
        Dynamic dynamic = new Dynamic();
        dynamic.setUserId(userId);
        dynamic.setContent(request.getContent());

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            dynamic.setImageUrls(JSON.toJSONString(request.getImageUrls()));
        }

        if (request.getVideoUrls() != null && !request.getVideoUrls().isEmpty()) {
            dynamic.setVideoUrls(JSON.toJSONString(request.getVideoUrls()));
        }

        dynamic.setLikeCount(0);
        dynamic.setCommentCount(0);
        dynamic.setIsApproved(1);

        dynamicMapper.insert(dynamic);
        log.info("动态创建成功: userId={}, dynamicId={}", userId, dynamic.getId());
        return dynamic;
    }

    @Override
    public List<Dynamic> getAllApprovedDynamics() {
        return dynamicMapper.selectApproved();
    }

    @Override
    public List<DynamicDetailResponse> getAllApprovedDynamicsWithDetails() {
        List<Dynamic> dynamics = dynamicMapper.selectApproved();
        List<DynamicDetailResponse> responses = new ArrayList<>();
        
        for (Dynamic dynamic : dynamics) {
            User user = userMapper.selectById(dynamic.getUserId());
            if (user == null && dynamic.getUserId().equals(ANONYMOUS_USER_ID)) {
                user = createAnonymousUser();
            }
            List<Comment> comments = commentMapper.selectByDynamicId(dynamic.getId());
            responses.add(convertToDetailResponse(dynamic, user, comments));
        }
        
        return responses;
    }

    @Override
    public List<DynamicDetailResponse> getMatchDiscussionsWithDetails() {
        List<Dynamic> dynamics = dynamicMapper.selectMatchDiscussions();
        List<DynamicDetailResponse> responses = new ArrayList<>();
        
        for (Dynamic dynamic : dynamics) {
            User user = userMapper.selectById(dynamic.getUserId());
            if (user == null && dynamic.getUserId().equals(ANONYMOUS_USER_ID)) {
                user = createAnonymousUser();
            }
            List<Comment> comments = commentMapper.selectByDynamicId(dynamic.getId());
            responses.add(convertToDetailResponse(dynamic, user, comments));
        }
        
        return responses;
    }

    @Override
    public DynamicDetailResponse getDynamicDetail(Long dynamicId) {
        Dynamic dynamic = dynamicMapper.selectById(dynamicId);
        if (dynamic == null) {
            throw new RuntimeException("动态不存在");
        }

        User user = userMapper.selectById(dynamic.getUserId());
        if (user == null && dynamic.getUserId().equals(ANONYMOUS_USER_ID)) {
            user = createAnonymousUser();
        }
        List<Comment> comments = commentMapper.selectByDynamicId(dynamicId);

        return convertToDetailResponse(dynamic, user, comments);
    }

    @Override
    @Transactional
    public Integer toggleLike(Long dynamicId, Long userId) {
        Dynamic dynamic = dynamicMapper.selectById(dynamicId);
        if (dynamic == null) {
            throw new RuntimeException("动态不存在");
        }

        boolean hasLiked = hasUserLiked(dynamicId, userId);
        
        if (hasLiked) {
            dynamic.setLikeCount(dynamic.getLikeCount() - 1);
            removeLikeRecord(dynamicId, userId);
            log.info("动态取消点赞: dynamicId={}, userId={}", dynamicId, userId);
        } else {
            dynamic.setLikeCount(dynamic.getLikeCount() + 1);
            addLikeRecord(dynamicId, userId);
            log.info("动态点赞: dynamicId={}, userId={}", dynamicId, userId);
        }
        
        dynamicMapper.updateById(dynamic);
        return dynamic.getLikeCount();
    }

    @Override
    @Transactional
    public void addComment(Long dynamicId, Long userId, String content, Long parentId) {
        Dynamic dynamic = dynamicMapper.selectById(dynamicId);
        if (dynamic == null) {
            throw new RuntimeException("动态不存在");
        }

        Comment comment = new Comment();
        comment.setDynamicId(dynamicId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId != null ? parentId : 0L);
        comment.setIsApproved(1);

        commentMapper.insert(comment);
        dynamic.setCommentCount(dynamic.getCommentCount() + 1);
        dynamicMapper.updateById(dynamic);
        log.info("评论添加成功: dynamicId={}, userId={}", dynamicId, userId);
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权编辑此评论");
        }

        comment.setContent(content);
        commentMapper.updateById(comment);
        log.info("评论更新成功: commentId={}, userId={}", commentId, userId);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }

        Dynamic dynamic = dynamicMapper.selectById(comment.getDynamicId());
        if (dynamic != null) {
            dynamic.setCommentCount(Math.max(0, dynamic.getCommentCount() - 1));
            dynamicMapper.updateById(dynamic);
        }

        commentMapper.deleteById(commentId);
        log.info("评论删除成功: commentId={}, userId={}", commentId, userId);
    }

    @Override
    @Transactional
    public void deleteDynamic(Long dynamicId) {
        Dynamic dynamic = dynamicMapper.selectById(dynamicId);
        if (dynamic == null) {
            throw new RuntimeException("动态不存在");
        }

        commentMapper.deleteByMap(Map.of("dynamic_id", dynamicId));
        dynamicMapper.deleteById(dynamicId);
        log.info("动态删除成功: {}", dynamicId);
    }

    @Override
    @Transactional
    public void approveDynamic(Long dynamicId) {
        Dynamic dynamic = dynamicMapper.selectById(dynamicId);
        if (dynamic == null) {
            throw new RuntimeException("动态不存在");
        }

        dynamic.setIsApproved(1);
        dynamicMapper.updateById(dynamic);
        log.info("动态审核通过: {}", dynamicId);
    }

    private DynamicDetailResponse convertToDetailResponse(Dynamic dynamic, User user, List<Comment> comments) {
        DynamicDetailResponse response = new DynamicDetailResponse();
        response.setId(dynamic.getId());
        response.setContent(dynamic.getContent());
        response.setType(dynamic.getType());

        List<String> imageUrls = new ArrayList<>();
        if (dynamic.getImageUrls() != null && !dynamic.getImageUrls().isEmpty()) {
            imageUrls = JSON.parseArray(dynamic.getImageUrls(), String.class);
        }
        response.setImageUrls(imageUrls);

        List<String> videoUrls = new ArrayList<>();
        if (dynamic.getVideoUrls() != null && !dynamic.getVideoUrls().isEmpty()) {
            videoUrls = JSON.parseArray(dynamic.getVideoUrls(), String.class);
        }
        response.setVideoUrls(videoUrls);

        response.setLikeCount(dynamic.getLikeCount());
        response.setCommentCount(dynamic.getCommentCount());
        response.setCreatedAt(dynamic.getCreatedAt());

        DynamicDetailResponse.UserInfo userInfo = new DynamicDetailResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        response.setUser(userInfo);

        List<DynamicDetailResponse.CommentInfo> commentInfos = new ArrayList<>();
        for (Comment comment : comments) {
            User commentUser = userMapper.selectById(comment.getUserId());
            if (commentUser == null && comment.getUserId().equals(ANONYMOUS_USER_ID)) {
                commentUser = createAnonymousUser();
            }
            DynamicDetailResponse.CommentInfo info = new DynamicDetailResponse.CommentInfo();
            info.setId(comment.getId());
            info.setContent(comment.getContent());
            info.setCreatedAt(comment.getCreatedAt());
            info.setParentId(comment.getParentId());

            DynamicDetailResponse.UserInfo commentUserInfo = new DynamicDetailResponse.UserInfo();
            commentUserInfo.setId(commentUser.getId());
            commentUserInfo.setNickname(commentUser.getNickname());
            commentUserInfo.setAvatarUrl(commentUser.getAvatarUrl());
            info.setUser(commentUserInfo);

            commentInfos.add(info);
        }
        response.setComments(commentInfos);

        return response;
    }

    private User createAnonymousUser() {
        User user = new User();
        user.setId(ANONYMOUS_USER_ID);
        user.setNickname("用户");
        user.setAvatarUrl("👤");
        return user;
    }

    private boolean hasUserLiked(Long dynamicId, Long userId) {
        return false;
    }

    private void addLikeRecord(Long dynamicId, Long userId) {
    }

    private void removeLikeRecord(Long dynamicId, Long userId) {
    }

}
