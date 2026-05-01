package com.jeffersonjr.checkout.service;

import com.jeffersonjr.checkout.client.BillingApiClient;
import com.jeffersonjr.checkout.client.CustomerApiClient;
import com.jeffersonjr.checkout.dto.BillingSummaryResponse;
import com.jeffersonjr.checkout.dto.CustomerResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CheckoutServiceTest {

    private final CustomerApiClient customerApiClient = Mockito.mock(CustomerApiClient.class);
    private final BillingApiClient billingApiClient = Mockito.mock(BillingApiClient.class);
    private final CheckoutService checkoutService = new CheckoutService(customerApiClient, billingApiClient);

    @Test
    void shouldReturnTrueWhenCustomerIsActiveAndBillingIsApproved() {
        when(customerApiClient.getCustomerById("cus-001"))
                .thenReturn(new CustomerResponse("cus-001", "Maria Silva", "12345678900", "ACTIVE"));
        when(billingApiClient.getBillingSummary("cus-001"))
                .thenReturn(new BillingSummaryResponse("cus-001", "APPROVED", new BigDecimal("1500.00"), "BRL"));

        var summary = checkoutService.getCheckoutSummary("cus-001");

        assertTrue(summary.canCheckout());
        assertEquals("Maria Silva", summary.customerName());
    }

    @Test
    void shouldReturnFalseWhenCustomerIsBlocked() {
        when(customerApiClient.getCustomerById("cus-002"))
                .thenReturn(new CustomerResponse("cus-002", "Joao Souza", "98765432100", "BLOCKED"));
        when(billingApiClient.getBillingSummary("cus-002"))
                .thenReturn(new BillingSummaryResponse("cus-002", "APPROVED", new BigDecimal("200.00"), "BRL"));

        var summary = checkoutService.getCheckoutSummary("cus-002");

        assertFalse(summary.canCheckout());
    }

    @Test
    void shouldReturnFalseWhenBillingIsRejected() {
        when(customerApiClient.getCustomerById("cus-003"))
                .thenReturn(new CustomerResponse("cus-003", "Ana Lima", "11122233344", "ACTIVE"));
        when(billingApiClient.getBillingSummary("cus-003"))
                .thenReturn(new BillingSummaryResponse("cus-003", "REJECTED", BigDecimal.ZERO, "BRL"));

        var summary = checkoutService.getCheckoutSummary("cus-003");

        assertFalse(summary.canCheckout());
    }

    @Test
    void shouldReturnFalseWhenAvailableLimitIsBelowMinimumCheckoutLimit() {
        when(customerApiClient.getCustomerById("cus-004"))
                .thenReturn(new CustomerResponse("cus-004", "Clara Nunes", "55566677788", "ACTIVE"));
        when(billingApiClient.getBillingSummary("cus-004"))
                .thenReturn(new BillingSummaryResponse("cus-004", "APPROVED", new BigDecimal("99.99"), "BRL"));

        var summary = checkoutService.getCheckoutSummary("cus-004");

        assertFalse(summary.canCheckout());
    }

    @Test
    void shouldReturnTrueWhenAvailableLimitMatchesMinimumCheckoutLimit() {
        when(customerApiClient.getCustomerById("cus-005"))
                .thenReturn(new CustomerResponse("cus-005", "Paula Costa", "44455566677", "ACTIVE"));
        when(billingApiClient.getBillingSummary("cus-005"))
                .thenReturn(new BillingSummaryResponse("cus-005", "APPROVED", new BigDecimal("100.00"), "BRL"));

        var summary = checkoutService.getCheckoutSummary("cus-005");

        assertTrue(summary.canCheckout());
    }

    @Test
    void shouldPropagateNotFoundWhenCustomerDoesNotExist() {
        when(customerApiClient.getCustomerById("cus-404")).thenThrow(new NotFoundException("missing customer"));

        assertThrows(NotFoundException.class, () -> checkoutService.getCheckoutSummary("cus-404"));
    }

    @Test
    void shouldReturnBadGatewayWhenCustomerApiFails() {
        when(customerApiClient.getCustomerById("cus-500")).thenThrow(new RuntimeException("customer unavailable"));

        var exception = assertThrows(WebApplicationException.class, () -> checkoutService.getCheckoutSummary("cus-500"));

        assertEquals(502, exception.getResponse().getStatus());
    }

    @Test
    void shouldReturnBadGatewayWhenBillingApiFails() {
        when(customerApiClient.getCustomerById("cus-001"))
                .thenReturn(new CustomerResponse("cus-001", "Maria Silva", "12345678900", "ACTIVE"));
        when(billingApiClient.getBillingSummary("cus-001")).thenThrow(new RuntimeException("billing unavailable"));

        var exception = assertThrows(WebApplicationException.class, () -> checkoutService.getCheckoutSummary("cus-001"));

        assertEquals(502, exception.getResponse().getStatus());
    }
}