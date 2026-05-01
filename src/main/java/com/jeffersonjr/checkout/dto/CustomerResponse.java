package com.jeffersonjr.checkout.dto;

public record CustomerResponse(
        String id,
        String name,
        String document,
        String status
) {
}