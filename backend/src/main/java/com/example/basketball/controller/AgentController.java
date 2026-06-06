package com.example.basketball.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.basketball.ai.DeepSeekClient;
import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.service.AgentService;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentService agentService;
    private final DeepSeekClient deepSeekClient;

    public AgentController(AgentService agentService, DeepSeekClient deepSeekClient) {
        this.agentService = agentService;
        this.deepSeekClient = deepSeekClient;
    }

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<String>> askQuestion(@RequestBody String question) {
        String answer = agentService.answerQuestion(question);
        return ResponseEntity.ok(ApiResponse.success(answer));
    }

    @GetMapping("/test-deepseek-pro")
    public ResponseEntity<ApiResponse<String>> testDeepSeekPro() {
        try {
            String result = deepSeekClient.testProConnection();
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("DeepSeek Pro连接测试失败: " + e.getMessage()));
        }
    }

    @PostMapping("/ask-pro")
    public ResponseEntity<ApiResponse<String>> askQuestionPro(@RequestBody String question) {
        try {
            String answer = deepSeekClient.chatPro("你是一个专业的AI助手，精通各种领域知识。", question);
            return ResponseEntity.ok(ApiResponse.success(answer));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("DeepSeek Pro调用失败: " + e.getMessage()));
        }
    }

}
