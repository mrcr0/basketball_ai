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
import com.example.basketball.entity.BasketballAction;
import com.example.basketball.service.BasketballActionService;

@RestController
@RequestMapping("/api/actions")
public class BasketballActionController {

    private final BasketballActionService actionService;

    public BasketballActionController(BasketballActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BasketballAction>>> getAllActions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        List<BasketballAction> actions;
        if (category != null) {
            actions = actionService.getActionsByCategory(category);
        } else if (difficulty != null) {
            actions = actionService.getActionsByDifficulty(difficulty);
        } else {
            actions = actionService.getAllActions();
        }
        return ResponseEntity.ok(ApiResponse.success(actions));
    }

    @GetMapping("/{actionId}")
    public ResponseEntity<ApiResponse<BasketballAction>> getAction(@PathVariable Long actionId) {
        BasketballAction action = actionService.getActionById(actionId);
        return ResponseEntity.ok(ApiResponse.success(action));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BasketballAction>> createAction(@RequestBody BasketballAction action) {
        BasketballAction created = actionService.createAction(action);
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @PutMapping("/{actionId}")
    public ResponseEntity<ApiResponse<BasketballAction>> updateAction(
            @PathVariable Long actionId,
            @RequestBody BasketballAction action) {
        BasketballAction updated = actionService.updateAction(actionId, action);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/{actionId}")
    public ResponseEntity<ApiResponse<Void>> deleteAction(@PathVariable Long actionId) {
        actionService.deleteAction(actionId);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

}
