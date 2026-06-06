package com.example.basketball.service;

import java.util.List;
import java.util.Map;

import com.example.basketball.dto.response.ChatResponse;
import com.example.basketball.entity.AiConversation;
import com.example.basketball.entity.AiMessage;

public interface AiChatService {

    ChatResponse chat(Long userId, String question, String sessionId);

    ChatResponse chatWithPro(Long userId, String question, String sessionId);

    List<AiConversation> getConversationList(Long userId);

    List<AiMessage> getMessages(Long userId, String sessionId);

    void deleteConversation(Long userId, String sessionId);

    void clearHistory(Long userId, String sessionId);

    Map<String, Object> getPerformanceMetrics();

    void resetPerformanceMetrics();
}
