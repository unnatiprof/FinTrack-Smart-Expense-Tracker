
package com.unnati.fintrack.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unnati.fintrack.entity.AuditLog;
import com.unnati.fintrack.services.AuditLogService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuditLogService auditLogService;

    public AdminController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // GET ALL ADMIN ACTIVITIES
    @GetMapping("/activities")
    public ResponseEntity<List<AuditLog>> getAllActivities() {

        return ResponseEntity.ok(
                auditLogService.findAll()
        );
    }
}