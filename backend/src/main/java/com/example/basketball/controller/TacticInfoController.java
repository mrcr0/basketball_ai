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
import com.example.basketball.entity.TacticInfo;
import com.example.basketball.service.TacticInfoService;

@RestController
@RequestMapping("/api/tactics")
public class TacticInfoController {

    private final TacticInfoService tacticService;

    public TacticInfoController(TacticInfoService tacticService) {
        this.tacticService = tacticService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TacticInfo>>> getAllTactics(
            @RequestParam(required = false) String type) {
        List<TacticInfo> tactics;
        if (type != null) {
            tactics = tacticService.getTacticsByType(type);
        } else {
            tactics = tacticService.getAllTactics();
        }
        return ResponseEntity.ok(ApiResponse.success(tactics));
    }

    @GetMapping("/{tacticId}")
    public ResponseEntity<ApiResponse<TacticInfo>> getTactic(@PathVariable Long tacticId) {
        TacticInfo tactic = tacticService.getTacticById(tacticId);
        return ResponseEntity.ok(ApiResponse.success(tactic));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TacticInfo>> createTactic(@RequestBody TacticInfo tactic) {
        TacticInfo created = tacticService.createTactic(tactic);
        return ResponseEntity.ok(ApiResponse.success("创建成功", created));
    }

    @PutMapping("/{tacticId}")
    public ResponseEntity<ApiResponse<TacticInfo>> updateTactic(
            @PathVariable Long tacticId,
            @RequestBody TacticInfo tactic) {
        TacticInfo updated = tacticService.updateTactic(tacticId, tactic);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    @DeleteMapping("/{tacticId}")
    public ResponseEntity<ApiResponse<Void>> deleteTactic(@PathVariable Long tacticId) {
        tacticService.deleteTactic(tacticId);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @GetMapping("/explain")
    public ResponseEntity<ApiResponse<String>> explainTactic(@RequestParam String name) {
        String explanation = tacticService.explainTacticByName(name);
        return ResponseEntity.ok(ApiResponse.success(explanation));
    }

}
