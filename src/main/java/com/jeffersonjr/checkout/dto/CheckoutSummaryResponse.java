package com.jeffersonjr.checkout.dto;

import java.math.BigDecimal;

public record CheckoutSummaryResponse(
        String customerId,
        String customerName,
        String customerDocument,
        String customerStatus,
        String billingStatus,
        BigDecimal availableLimit,
        boolean canCheckout
) {
}