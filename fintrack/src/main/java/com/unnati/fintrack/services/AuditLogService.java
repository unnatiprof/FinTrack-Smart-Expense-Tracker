package com.unnati.fintrack.services;

import java.util.List;

import com.unnati.fintrack.entity.AuditLog;

public interface AuditLogService {

    List<AuditLog> findAll();

    AuditLog findById(Long id);

    AuditLog save(AuditLog auditLog);

    void deleteById(Long id);

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByUserEmail(String userEmail);
}