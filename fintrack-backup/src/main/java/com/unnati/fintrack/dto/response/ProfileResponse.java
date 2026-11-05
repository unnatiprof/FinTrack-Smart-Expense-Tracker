package com.unnati.fintrack.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponse {

    private Long userId;
    private String name;
    private String email;
    private String currency;
    private BigDecimal monthlyIncome;
    private String role;
    private String status;
    private LocalDateTime lastLogin;
}