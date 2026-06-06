package com.example.basketball.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.basketball.dto.request.ChatRequest;
import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.dto.response.ChatResponse;
import com.example.basketball.dto.response.ConversationHistoryResponse;
import com.example.basketball.entity.AiConversation;
import com.example.basketball.entity.AiMessage;
import com.example.basketball.service.AiChatService;
import com.example.basketball.util.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Long ANONYMOUS_USER_ID = 0L;

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ChatResponse>> sendMessage(
            @Valid @RequestBody ChatRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);

        ChatResponse response = aiChatService.chat(
                userId,
                request.getQuestion(),
                request.getSessionId()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationHistoryResponse>>> getConversations(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);

        List<AiConversation> conversations = aiChatService.getConversationList(userId);

        List<ConversationHistoryResponse> responses = conversations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ApiResponse<ConversationHistoryResponse>> getHistory(
            @PathVariable String sessionId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);

        List<AiMessage> messages = aiChatService.getMessages(userId, sessionId);

        ConversationHistoryResponse response = new ConversationHistoryResponse();
        response.setSessionId(sessionId);

        if (!messages.isEmpty()) {
            AiMessage firstMessage = messages.get(0);
            response.setTopic(firstMessage.getContent().length() > 50 ?
                    firstMessage.getContent().substring(0, 50) : firstMessage.getContent());
            response.setDomain(firstMessage.getDomain());
            response.setCreatedAt(firstMessage.getCreatedAt());
        }

        response.setMessageCount(messages.size());
        response.setLastMessageAt(messages.isEmpty() ? null : messages.get(messages.size() - 1).getCreatedAt());

        List<ConversationHistoryResponse.MessageItem> items = messages.stream()
                .map(msg -> {
                    ConversationHistoryResponse.MessageItem item = new ConversationHistoryResponse.MessageItem();
                    item.setRole(msg.getRole());
                    item.setContent(msg.getContent());
                    item.setCreatedAt(msg.getCreatedAt());
                    return item;
                })
                .collect(Collectors.toList());

        response.setMessages(items);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/conversation/{sessionId}")
    public ResponseEntity<ApiResponse<String>> deleteConversation(
            @PathVariable String sessionId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);

        aiChatService.deleteConversation(userId, sessionId);

        return ResponseEntity.ok(ApiResponse.success("对话已删除"));
    }

    @DeleteMapping("/history/{sessionId}")
    public ResponseEntity<ApiResponse<String>> clearHistory(
            @PathVariable String sessionId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);

        aiChatService.clearHistory(userId, sessionId);

        return ResponseEntity.ok(ApiResponse.success("历史记录已清空"));
    }

    @PostMapping("/send-pro")
    public ResponseEntity<ApiResponse<ChatResponse>> sendMessagePro(
            @Valid @RequestBody ChatRequest request,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);

        ChatResponse response = aiChatService.chatWithPro(
                userId,
                request.getQuestion(),
                request.getSessionId()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        Map<String, Object> metrics = aiChatService.getPerformanceMetrics();
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @DeleteMapping("/metrics")
    public ResponseEntity<ApiResponse<String>> resetPerformanceMetrics() {
        aiChatService.resetPerformanceMetrics();
        return ResponseEntity.ok(ApiResponse.success("性能指标已重置"));
    }

    private Long extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwtToken = token.substring(7);
                return jwtUtil.getUserIdFromToken(jwtToken);
            } catch (Exception e) {
                return ANONYMOUS_USER_ID;
            }
        }
        return ANONYMOUS_USER_ID;
    }

    private ConversationHistoryResponse convertToResponse(AiConversation conversation) {
        ConversationHistoryResponse response = new ConversationHistoryResponse();
        response.setSessionId(conversation.getSessionId());
        response.setTopic(conversation.getTopic());
        response.setDomain(conversation.getDomain());
        response.setMessageCount(conversation.getMessageCount());
        response.setCreatedAt(conversation.getCreatedAt());
        response.setLastMessageAt(conversation.getLastMessageAt());
        return response;
    }
}
