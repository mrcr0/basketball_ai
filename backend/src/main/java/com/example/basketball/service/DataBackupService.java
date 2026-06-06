package com.example.basketball.service;

import com.example.basketball.entity.DataBackup;

import java.util.List;

public interface DataBackupService {

    DataBackup createBackup(String backupType);

    void restoreBackup(Long backupId);

    List<DataBackup> listBackups();

    DataBackup getBackupById(Long id);

    void deleteBackup(Long id);
}