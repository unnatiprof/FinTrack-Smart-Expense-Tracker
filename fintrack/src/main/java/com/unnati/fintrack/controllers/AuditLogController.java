package com.unnati.fintrack.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unnati.fintrack.entity.AuditLog;
import com.unnati.fintrack.services.AuditLogService;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> findAll() {
        return ResponseEntity.ok(
                auditLogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                auditLogService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AuditLog> save(
            @RequestBody AuditLog auditLog) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(auditLogService.save(auditLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable Long id) {

        auditLogService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> findByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                auditLogService.findByUserId(userId));
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> findByAction(
            @PathVariable String action) {

        return ResponseEntity.ok(
                auditLogService.findByAction(action));
    }

    @GetMapping("/email/{userEmail}")
    public ResponseEntity<List<AuditLog>> findByUserEmail(
            @PathVariable String userEmail) {

        return ResponseEntity.ok(
                auditLogService.findByUserEmail(userEmail));
    }
}