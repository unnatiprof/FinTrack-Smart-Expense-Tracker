package com.unnati.fintrack.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.unnati.fintrack.entity.AuditLog;
import com.unnati.fintrack.exception.ResourceNotFoundException;
import com.unnati.fintrack.repository.AuditLogRepository;
import com.unnati.fintrack.services.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }

    @Override
    public AuditLog findById(Long id) {

        return auditLogRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Audit log not found with id: " + id));
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    @Override
    public void deleteById(Long id) {

        AuditLog existingAuditLog = findById(id);

        auditLogRepository.delete(existingAuditLog);
    }

    @Override
    public List<AuditLog> findByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    @Override
    public List<AuditLog> findByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    @Override
    public List<AuditLog> findByUserEmail(String userEmail) {
        return auditLogRepository.findByUserEmail(userEmail);
    }
}