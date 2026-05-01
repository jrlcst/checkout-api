package com.jeffersonjr.checkout.service;

import com.jeffersonjr.checkout.client.BillingApiClient;
import com.jeffersonjr.checkout.client.CustomerApiClient;
import com.jeffersonjr.checkout.dto.BillingSummaryResponse;
import com.jeffersonjr.checkout.dto.CheckoutSummaryResponse;
import com.jeffersonjr.checkout.dto.CustomerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;

@ApplicationScoped
public class CheckoutService {

    static final BigDecimal MINIMUM_CHECKOUT_LIMIT = new BigDecimal("100.00");

    private final CustomerApiClient customerApiClient;
    private final BillingApiClient billingApiClient;

    @Inject
    public CheckoutService(
            @RestClient CustomerApiClient customerApiClient,
            @RestClient BillingApiClient billingApiClient
    ) {
        this.customerApiClient = customerApiClient;
        this.billingApiClient = billingApiClient;
    }

    public CheckoutSummaryResponse getCheckoutSummary(String customerId) {
        final CustomerResponse customer = fetchCustomer(customerId);
        final BillingSummaryResponse billingSummary = fetchBillingSummary(customerId);

        return new CheckoutSummaryResponse(
                customer.id(),
                customer.name(),
                customer.document(),
                customer.status(),
                billingSummary.status(),
                billingSummary.availableLimit(),
                canCheckout(customer, billingSummary)
        );
    }

    boolean canCheckout(final CustomerResponse customer, final BillingSummaryResponse billingSummary) {
        return "ACTIVE".equals(customer.status())
                && "APPROVED".equals(billingSummary.status())
                && billingSummary.availableLimit().compareTo(MINIMUM_CHECKOUT_LIMIT) >= 0;
    }

    private CustomerResponse fetchCustomer(final String customerId) {
        try {
            return customerApiClient.getCustomerById(customerId);
        } catch (NotFoundException exception) {
            throw new NotFoundException("Customer not found for checkout: " + customerId, exception);
        } catch (WebApplicationException exception) {
            throw badGateway("Customer API returned an unexpected response", exception);
        } catch (RuntimeException exception) {
            throw badGateway("Customer API is unavailable", exception);
        }
    }

    private BillingSummaryResponse fetchBillingSummary(String customerId) {
        try {
            return billingApiClient.getBillingSummary(customerId);
        } catch (NotFoundException exception) {
            throw new NotFoundException("Billing summary not found for checkout: " + customerId, exception);
        } catch (WebApplicationException exception) {
            throw badGateway("Billing API returned an unexpected response", exception);
        } catch (RuntimeException exception) {
            throw badGateway("Billing API is unavailable", exception);
        }
    }

    private WebApplicationException badGateway(final String message, final Exception cause) {
        return new WebApplicationException(message, cause, Response.Status.BAD_GATEWAY);
    }
}