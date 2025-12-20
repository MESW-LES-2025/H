package com.lernia.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    private String paymentMethod; // "MOCK_CARD", "MOCK_PAYPAL"
}
