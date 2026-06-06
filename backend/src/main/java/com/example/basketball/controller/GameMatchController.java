package com.example.basketball.controller;

import com.example.basketball.dto.response.ApiResponse;
import com.example.basketball.entity.GameMatch;
import com.example.basketball.service.GameMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 比赛控制器
 */
@RestController
@RequestMapping("/api/match")
public class GameMatchController {

    @Autowired
    private GameMatchService gameMatchService;

    /**
     * 获取比赛信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GameMatch>> getMatch(@PathVariable Long id) {
        GameMatch match = gameMatchService.getMatchById(id);
        if (match == null) {
            return ResponseEntity.ok(ApiResponse.error("比赛不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success("获取成功", match));
    }

    /**
     * 创建模拟比赛
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GameMatch>> createMatch() {
        GameMatch match = gameMatchService.createSimulatedMatch();
        return ResponseEntity.ok(ApiResponse.success("创建成功", match));
    }

    /**
     * 更新比赛数据（模拟实时更新）
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<GameMatch>> updateMatch(@PathVariable Long id) {
        GameMatch match = gameMatchService.updateMatchData(id);
        if (match == null) {
            return ResponseEntity.ok(ApiResponse.error("比赛不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success("更新成功", match));
    }

    /**
     * 开始/暂停比赛
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<GameMatch>> toggleMatch(@PathVariable Long id) {
        GameMatch match = gameMatchService.toggleMatch(id);
        if (match == null) {
            return ResponseEntity.ok(ApiResponse.error("比赛不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success("操作成功", match));
    }

    /**
     * 重置比赛
     */
    @PutMapping("/{id}/reset")
    public ResponseEntity<ApiResponse<GameMatch>> resetMatch(@PathVariable Long id) {
        GameMatch match = gameMatchService.resetMatch(id);
        if (match == null) {
            return ResponseEntity.ok(ApiResponse.error("比赛不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success("重置成功", match));
    }
}