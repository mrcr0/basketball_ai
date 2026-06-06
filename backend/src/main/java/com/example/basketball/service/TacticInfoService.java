package com.example.basketball.service;

import com.example.basketball.entity.TacticInfo;

import java.util.List;

public interface TacticInfoService {

    List<TacticInfo> getAllTactics();

    List<TacticInfo> getTacticsByType(String type);

    TacticInfo getTacticById(Long tacticId);

    TacticInfo createTactic(TacticInfo tactic);

    TacticInfo updateTactic(Long tacticId, TacticInfo tactic);

    void deleteTactic(Long tacticId);

    String explainTacticByName(String tacticName);

}
