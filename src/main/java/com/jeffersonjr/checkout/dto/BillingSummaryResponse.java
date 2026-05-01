package com.jeffersonjr.checkout.dto;

import java.math.BigDecimal;

public record BillingSummaryResponse(
        String customerId,
        String status,
        BigDecimal availableLimit,
        String currency
) {
}