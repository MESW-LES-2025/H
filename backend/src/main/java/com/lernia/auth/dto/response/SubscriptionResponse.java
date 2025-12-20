package com.lernia.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private String message;
    private String status; // "success" | "error"
    private String userRole;
    private LocalDate premiumStartDate;

    public SubscriptionResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
