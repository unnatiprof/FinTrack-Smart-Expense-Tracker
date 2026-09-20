package com.unnati.fintrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unnati.fintrack.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByUserEmail(String userEmail);
}