package com.example.basketball.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.basketball.ai.DeepSeekClient;
import com.example.basketball.dto.response.ChatResponse;
import com.example.basketball.entity.AiConversation;
import com.example.basketball.entity.AiMessage;
import com.example.basketball.mapper.AiConversationMapper;
import com.example.basketball.mapper.AiMessageMapper;
import com.example.basketball.service.AiChatService;
import com.example.basketball.service.ContentSafetyService;

@Service
public class AiChatServiceImpl implements AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);
    private static final int MAX_CONTEXT_MESSAGES = 15;
    private static final int MAX_TOKEN_ESTIMATE = 4000;
    private static final int MAX_QUESTION_LENGTH = 2000;

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Autowired
    private ContentSafetyService contentSafetyService;

    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private AiMessageMapper messageMapper;

    private final Map<String, Long> sessionTimeout = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000;

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxResponseTime = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> domainRequestCounts = new ConcurrentHashMap<>();

    private static final String[] UNANSWERABLE_TOPICS = {
            "隐私", "密码", "验证码", "银行", "转账", "支付",
            "政治", "宗教", "军事机密", "个人信息", "敏感信息"
    };

    private static final String UNANSWERABLE_RESPONSE = 
            "抱歉，关于这个问题我无法提供回答。原因可能是：\n" +
            "1. 问题涉及隐私、安全或敏感信息\n" +
            "2. 问题超出了我的知识范围\n" +
            "3. 问题表述不够清晰\n\n" +
            "请尝试其他话题，我会尽力为您解答！";

    private static final String NETWORK_ERROR_RESPONSE = 
            "网络异常，请检查网络连接后重试。如果问题持续存在，请稍后再试。";

    private static final String SERVICE_UNAVAILABLE_RESPONSE = 
            "AI服务暂时不可用，请稍后重试。我们正在努力恢复服务。";

    private static final String RATE_LIMIT_RESPONSE = 
            "请求过于频繁，请稍等片刻后再试。";

    private static final int RATE_LIMIT_MAX_REQUESTS = 30;
    private static final int RATE_LIMIT_WINDOW_SECONDS = 60;
    private final ConcurrentHashMap<String, List<Long>> requestTimestamps = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public ChatResponse chat(Long userId, String question, String sessionId) {
        return chatInternal(userId, question, sessionId, false);
    }

    @Override
    @Transactional
    public ChatResponse chatWithPro(Long userId, String question, String sessionId) {
        return chatInternal(userId, question, sessionId, true);
    }

    private ChatResponse chatInternal(Long userId, String question, String sessionId, boolean usePro) {
        long startTime = System.currentTimeMillis();
        totalRequests.incrementAndGet();
        
        String clientKey = userId != null ? userId.toString() : "anonymous";
        
        ChatResponse response = new ChatResponse();

        try {
            if (question == null || question.trim().isEmpty()) {
                response = buildErrorResponse(sessionId, "问题不能为空", "invalid");
                recordMetrics(startTime, false, "invalid");
                return response;
            }

            if (question.length() > MAX_QUESTION_LENGTH) {
                response = buildErrorResponse(sessionId, "问题过长，请控制在2000字以内", "too_long");
                recordMetrics(startTime, false, "invalid");
                return response;
            }

            if (!checkRateLimit(clientKey)) {
                response = buildErrorResponse(sessionId, RATE_LIMIT_RESPONSE, "rate_limit");
                recordMetrics(startTime, false, "rate_limit");
                return response;
            }

            String filteredQuestion = contentSafetyService.filterPersonalInfo(question);

            ContentSafetyService.SafetyCheckResult safetyResult = contentSafetyService.checkContent(filteredQuestion);
            if (!safetyResult.isSafe()) {
                response = buildErrorResponse(sessionId, 
                    "抱歉，您的问题包含不当内容，无法回答。请修改问题后重新提问。", "blocked");
                recordMetrics(startTime, false, "blocked");
                return response;
            }

            if (isUnanswerableTopic(filteredQuestion)) {
                response = buildErrorResponse(sessionId, UNANSWERABLE_RESPONSE, "unanswerable");
                recordMetrics(startTime, false, "unanswerable");
                return response;
            }

            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            String detectedDomain = contentSafetyService.detectDomain(filteredQuestion);

            AiConversation conversation = getOrCreateConversation(userId, sessionId, filteredQuestion, detectedDomain);

            List<AiMessage> historyMessages = getRecentMessages(sessionId, MAX_CONTEXT_MESSAGES);

            String aiResponse = callAI(filteredQuestion, historyMessages, detectedDomain, usePro);

            saveMessages(conversation.getId(), sessionId, "user", filteredQuestion, detectedDomain);
            saveMessages(conversation.getId(), sessionId, "assistant", aiResponse, detectedDomain);

            updateConversationActivity(conversation.getId());

            response.setSessionId(sessionId);
            response.setAnswer(aiResponse);
            response.setDomain(detectedDomain);
            response.setDomainLabel(contentSafetyService.getDomainLabel(detectedDomain));
            response.setTimestamp(LocalDateTime.now());
            response.setTokenCount(estimateTokenCount(filteredQuestion + aiResponse));
            response.setIsComplete(true);
            response.setModelType(usePro ? "pro" : "standard");

            recordMetrics(startTime, true, detectedDomain);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Chat completed in {}ms, session: {}, domain: {}, model: {}", 
                duration, sessionId, detectedDomain, usePro ? "pro" : "standard");

            return response;

        } catch (Exception e) {
            log.error("Chat error for user {}: {}", userId, e.getMessage(), e);
            
            String errorType = classifyError(e);
            String errorMessage = getErrorMessage(errorType);
            
            response.setSessionId(sessionId);
            response.setAnswer(errorMessage);
            response.setDomain("error");
            response.setTimestamp(LocalDateTime.now());
            response.setIsComplete(false);
            
            recordMetrics(startTime, false, errorType);
            return response;
        }
    }

    private boolean checkRateLimit(String clientKey) {
        long now = System.currentTimeMillis();
        List<Long> timestamps = requestTimestamps.computeIfAbsent(clientKey, k -> new ArrayList<>());
        
        timestamps.removeIf(ts -> now - ts > RATE_LIMIT_WINDOW_SECONDS * 1000);
        
        if (timestamps.size() >= RATE_LIMIT_MAX_REQUESTS) {
            return false;
        }
        
        timestamps.add(now);
        return true;
    }

    private boolean isUnanswerableTopic(String question) {
        String lowerQuestion = question.toLowerCase();
        for (String topic : UNANSWERABLE_TOPICS) {
            if (lowerQuestion.contains(topic)) {
                return true;
            }
        }
        return false;
    }

    private String callAI(String question, List<AiMessage> history, String domain, boolean usePro) {
        String systemPrompt = buildSystemPrompt(domain);

        String fullQuestion = buildContextualQuestion(question, history);

        try {
            if (usePro) {
                return deepSeekClient.chatPro(systemPrompt, fullQuestion);
            } else {
                return deepSeekClient.chat(systemPrompt, fullQuestion);
            }
        } catch (Exception e) {
            log.error("AI API call failed (usePro={}): {}", usePro, e.getMessage());
            throw e;
        }
    }

    private String buildContextualQuestion(String question, List<AiMessage> history) {
        if (history == null || history.isEmpty()) {
            return question;
        }

        StringBuilder contextBuilder = new StringBuilder();
        int totalTokens = 0;
        
        for (int i = history.size() - 1; i >= 0; i--) {
            AiMessage msg = history.get(i);
            int msgTokens = estimateTokenCount(msg.getContent());
            
            if (totalTokens + msgTokens > MAX_TOKEN_ESTIMATE - 500) {
                break;
            }
            
            contextBuilder.insert(0, msg.getRole() + ": " + msg.getContent() + "\n");
            totalTokens += msgTokens;
        }

        return "对话历史:\n" + contextBuilder.toString() + "\n当前问题: " + question;
    }

    private ChatResponse buildErrorResponse(String sessionId, String message, String domain) {
        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId != null ? sessionId : UUID.randomUUID().toString());
        response.setAnswer(message);
        response.setDomain(domain);
        response.setTimestamp(LocalDateTime.now());
        response.setIsComplete(false);
        return response;
    }

    private String classifyError(Exception e) {
        String message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        
        if (message.contains("network") || message.contains("connection") || message.contains("timeout")) {
            return "network_error";
        }
        if (message.contains("unavailable") || message.contains("service")) {
            return "service_unavailable";
        }
        if (message.contains("rate limit") || message.contains("too many")) {
            return "rate_limit";
        }
        if (message.contains("api key") || message.contains("auth")) {
            return "auth_error";
        }
        return "unknown_error";
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case "network_error":
                return NETWORK_ERROR_RESPONSE;
            case "service_unavailable":
                return SERVICE_UNAVAILABLE_RESPONSE;
            case "rate_limit":
                return RATE_LIMIT_RESPONSE;
            default:
                return SERVICE_UNAVAILABLE_RESPONSE;
        }
    }

    private void recordMetrics(long startTime, boolean success, String domain) {
        long duration = System.currentTimeMillis() - startTime;
        
        if (success) {
            successfulRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }
        
        totalResponseTime.addAndGet(duration);
        
        if (duration < minResponseTime.get()) {
            minResponseTime.compareAndSet(minResponseTime.get(), duration);
        }
        if (duration > maxResponseTime.get()) {
            maxResponseTime.compareAndSet(maxResponseTime.get(), duration);
        }
        
        domainRequestCounts.computeIfAbsent(domain, k -> new AtomicLong(0)).incrementAndGet();
    }

    private String buildSystemPrompt(String domain) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个友好、专业、乐于助人的AI助手。请遵循以下原则:\n");
        prompt.append("1. 回答准确、简洁、有条理\n");
        prompt.append("2. 对于不确定的问题，坦诚说明\n");
        prompt.append("3. 适当使用emoji增加趣味性\n");
        prompt.append("4. 如果需要，可以提供步骤或示例\n");
        prompt.append("5. 保持对话的连贯性和上下文理解\n\n");

        switch (domain) {
            case "basketball":
                prompt.append("同时你也是一位专业的篮球教练，可以提供专业的篮球技术指导、战术分析和训练建议。");
                break;
            case "history":
                prompt.append("请以史实为依据，客观中立地回答历史问题。");
                break;
            case "science":
                prompt.append("请以科学严谨的态度回答科学技术问题，必要时可以提供数据支持。");
                break;
            case "technology":
                prompt.append("请提供准确、实用的技术信息，包括代码示例（如果适用）。");
                break;
            default:
                prompt.append("你可以回答各种领域的问题，包括生活常识、学习方法、健康建议等。");
                break;
        }

        return prompt.toString();
    }

    private AiConversation getOrCreateConversation(Long userId, String sessionId, String question, String domain) {
        AiConversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<AiConversation>()
                        .eq(AiConversation::getUserId, userId)
                        .eq(AiConversation::getSessionId, sessionId)
        );

        if (conversation == null) {
            conversation = new AiConversation();
            conversation.setUserId(userId);
            conversation.setSessionId(sessionId);
            conversation.setTopic(question.length() > 50 ? question.substring(0, 50) : question);
            conversation.setDomain(domain);
            conversation.setMessageCount(0);
            conversation.setCreatedAt(LocalDateTime.now());
            conversation.setUpdatedAt(LocalDateTime.now());
            conversation.setLastMessageAt(LocalDateTime.now());
            conversationMapper.insert(conversation);
        }

        return conversation;
    }

    private List<AiMessage> getRecentMessages(String sessionId, int limit) {
        List<AiMessage> allMessages = messageMapper.selectList(
                new LambdaQueryWrapper<AiMessage>()
                        .eq(AiMessage::getSessionId, sessionId)
                        .orderByDesc(AiMessage::getCreatedAt)
                        .last("LIMIT " + (limit * 2))
        );

        Collections.reverse(allMessages);

        int totalTokens = 0;
        List<AiMessage> contextMessages = new ArrayList<>();
        for (AiMessage msg : allMessages) {
            int msgTokens = estimateTokenCount(msg.getContent());
            if (totalTokens + msgTokens > MAX_TOKEN_ESTIMATE) {
                break;
            }
            contextMessages.add(0, msg);
        }

        return contextMessages;
    }

    private void saveMessages(Long conversationId, String sessionId, String role, String content, String domain) {
        AiMessage message = new AiMessage();
        message.setConversationId(conversationId);
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setDomain(domain);
        message.setCreatedAt(LocalDateTime.now());
        message.setTokenCount(estimateTokenCount(content));
        messageMapper.insert(message);

        conversationMapper.update(null,
                new LambdaUpdateWrapper<AiConversation>()
                        .eq(AiConversation::getId, conversationId)
                        .set(AiConversation::getMessageCount, messageMapper.selectCount(
                                new LambdaQueryWrapper<AiMessage>()
                                        .eq(AiMessage::getConversationId, conversationId)
                        ).intValue())
                        .set(AiConversation::getLastMessageAt, LocalDateTime.now())
                        .set(AiConversation::getUpdatedAt, LocalDateTime.now())
        );
    }

    private void updateConversationActivity(Long conversationId) {
        conversationMapper.update(null,
                new LambdaUpdateWrapper<AiConversation>()
                        .eq(AiConversation::getId, conversationId)
                        .set(AiConversation::getLastMessageAt, LocalDateTime.now())
        );
    }

    private int estimateTokenCount(String text) {
        return text == null ? 0 : text.length() / 4;
    }

    @Override
    public List<AiConversation> getConversationList(Long userId) {
        return conversationMapper.selectList(
                new LambdaQueryWrapper<AiConversation>()
                        .eq(AiConversation::getUserId, userId)
                        .orderByDesc(AiConversation::getLastMessageAt)
                        .last("LIMIT 50")
        );
    }

    @Override
    public List<AiMessage> getMessages(Long userId, String sessionId) {
        AiConversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<AiConversation>()
                        .eq(AiConversation::getUserId, userId)
                        .eq(AiConversation::getSessionId, sessionId)
        );

        if (conversation == null) {
            return new ArrayList<>();
        }

        return messageMapper.selectList(
                new LambdaQueryWrapper<AiMessage>()
                        .eq(AiMessage::getConversationId, conversation.getId())
                        .orderByAsc(AiMessage::getCreatedAt)
        );
    }

    @Override
    @Transactional
    public void deleteConversation(Long userId, String sessionId) {
        AiConversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<AiConversation>()
                        .eq(AiConversation::getUserId, userId)
                        .eq(AiConversation::getSessionId, sessionId)
        );

        if (conversation != null) {
            messageMapper.delete(
                    new LambdaQueryWrapper<AiMessage>()
                            .eq(AiMessage::getConversationId, conversation.getId())
            );
            conversationMapper.deleteById(conversation.getId());
        }
    }

    @Override
    @Transactional
    public void clearHistory(Long userId, String sessionId) {
        AiConversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<AiConversation>()
                        .eq(AiConversation::getUserId, userId)
                        .eq(AiConversation::getSessionId, sessionId)
        );

        if (conversation != null) {
            messageMapper.delete(
                    new LambdaQueryWrapper<AiMessage>()
                            .eq(AiMessage::getConversationId, conversation.getId())
            );

            conversation.setMessageCount(0);
            conversation.setTopic("");
            conversationMapper.updateById(conversation);
        }
    }

    @Override
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        long total = totalRequests.get();
        long success = successfulRequests.get();
        long failed = failedRequests.get();
        long totalTime = totalResponseTime.get();
        
        metrics.put("totalRequests", total);
        metrics.put("successfulRequests", success);
        metrics.put("failedRequests", failed);
        metrics.put("successRate", total > 0 ? String.format("%.2f%%", (success * 100.0) / total) : "0%");
        
        if (total > 0) {
            metrics.put("averageResponseTimeMs", totalTime / total);
        } else {
            metrics.put("averageResponseTimeMs", 0);
        }
        
        metrics.put("minResponseTimeMs", minResponseTime.get() == Long.MAX_VALUE ? 0 : minResponseTime.get());
        metrics.put("maxResponseTimeMs", maxResponseTime.get());
        
        Map<String, Long> domainCounts = new HashMap<>();
        domainRequestCounts.forEach((domain, count) -> domainCounts.put(domain, count.get()));
        metrics.put("domainRequestCounts", domainCounts);
        
        metrics.put("timestamp", LocalDateTime.now().toString());
        
        return metrics;
    }

    @Override
    public void resetPerformanceMetrics() {
        totalRequests.set(0);
        successfulRequests.set(0);
        failedRequests.set(0);
        totalResponseTime.set(0);
        minResponseTime.set(Long.MAX_VALUE);
        maxResponseTime.set(0);
        domainRequestCounts.clear();
        log.info("Performance metrics reset");
    }
}
