package com.example.basketball.controller;

import java.util.List;

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

import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.entity.BasketballKnowledge;
import com.example.basketball.service.BasketballKnowledgeService;

@RestController
@RequestMapping("/api/knowledge")
public class BasketballKnowledgeController {

    private final BasketballKnowledgeService knowledgeService;

    public BasketballKnowledgeController(BasketballKnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BasketballKnowledge>>> getAllKnowledge(
            @RequestParam(required = false) String category) {
        List<BasketballKnowledge> knowledge;
        if (category != null && !category.isEmpty()) {
            knowledge = knowledgeService.getKnowledgeByCategory(category);
        } else {
            knowledge = knowledgeService.getAllKnowledge();
        }
        return ResponseEntity.ok(ApiResponse.success(knowledge));
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<BasketballKnowledge>>> getHotKnowledge(
            @RequestParam(defaultValue = "10") int limit) {
        List<BasketballKnowledge> hot = knowledgeService.getHotKnowledge(limit);
        return ResponseEntity.ok(ApiResponse.success(hot));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BasketballKnowledge>> getKnowledge(@PathVariable Long id) {
        BasketballKnowledge knowledge = knowledgeService.getKnowledgeById(id);
        return ResponseEntity.ok(ApiResponse.success(knowledge));
    }

    @GetMapping("/subcategories")
    public ResponseEntity<ApiResponse<List<String>>> getSubCategories(
            @RequestParam String category) {
        List<String> subCategories = knowledgeService.getSubCategories(category);
        return ResponseEntity.ok(ApiResponse.success(subCategories));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BasketballKnowledge>> createKnowledge(
            @RequestBody BasketballKnowledge knowledge) {
        BasketballKnowledge created = knowledgeService.createKnowledge(knowledge);
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BasketballKnowledge>> updateKnowledge(
            @PathVariable Long id, @RequestBody BasketballKnowledge knowledge) {
        BasketballKnowledge updated = knowledgeService.updateKnowledge(id, knowledge);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKnowledge(@PathVariable Long id) {
        knowledgeService.deleteKnowledge(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}
