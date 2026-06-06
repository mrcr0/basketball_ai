package com.example.basketball.service;

import com.example.basketball.entity.BasketballAction;

import java.util.List;

public interface BasketballActionService {

    List<BasketballAction> getAllActions();

    List<BasketballAction> getActionsByCategory(String category);

    List<BasketballAction> getActionsByDifficulty(String difficulty);

    BasketballAction getActionById(Long actionId);

    BasketballAction createAction(BasketballAction action);

    BasketballAction updateAction(Long actionId, BasketballAction action);

    void deleteAction(Long actionId);

}
