package com.jeffersonjr.checkout.resource;

import com.jeffersonjr.checkout.client.BillingApiClient;
import com.jeffersonjr.checkout.client.CustomerApiClient;
import com.jeffersonjr.checkout.dto.BillingSummaryResponse;
import com.jeffersonjr.checkout.dto.CustomerResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@QuarkusTest
class CheckoutResourceTest {

    @InjectMock
    @RestClient
    CustomerApiClient customerApiClient;

    @InjectMock
    @RestClient
    BillingApiClient billingApiClient;

    @Test
    void shouldReturnCheckoutSummaryWhenEligible() {
        when(customerApiClient.getCustomerById("cus-001"))
                .thenReturn(new CustomerResponse("cus-001", "Maria Silva", "12345678900", "ACTIVE"));
        when(billingApiClient.getBillingSummary("cus-001"))
                .thenReturn(new BillingSummaryResponse("cus-001", "APPROVED", new BigDecimal("1500.00"), "BRL"));

        given()
                .when().get("/v1/checkouts/cus-001/summary")
                .then()
                .statusCode(200)
                .body("customerId", is("cus-001"))
                .body("customerName", is("Maria Silva"))
                .body("customerDocument", is("12345678900"))
                .body("customerStatus", is("ACTIVE"))
                .body("billingStatus", is("APPROVED"))
                .body("canCheckout", is(true));
    }

//    @Test
//    void shouldReturnFalseWhenCustomerIsBlocked() {
//        when(customerApiClient.getCustomerById("cus-002"))
//                .thenReturn(new CustomerResponse("cus-002", "Joao Souza", "98765432100", "BLOCKED"));
//        when(billingApiClient.getBillingSummary("cus-002"))
//                .thenReturn(new BillingSummaryResponse("cus-002", "APPROVED", new BigDecimal("500.00"), "BRL"));
//
//        given()
//                .when().get("/v1/checkouts/cus-002/summary")
//                .then()
//                .statusCode(200)
//                .body("canCheckout", is(false));
//    }
//
//    @Test
//    void shouldReturnFalseWhenBillingIsRejected() {
//        when(customerApiClient.getCustomerById("cus-003"))
//                .thenReturn(new CustomerResponse("cus-003", "Ana Lima", "11122233344", "ACTIVE"));
//        when(billingApiClient.getBillingSummary("cus-003"))
//                .thenReturn(new BillingSummaryResponse("cus-003", "REJECTED", BigDecimal.ZERO, "BRL"));
//
//        given()
//                .when().get("/v1/checkouts/cus-003/summary")
//                .then()
//                .statusCode(200)
//                .body("billingStatus", is("REJECTED"))
//                .body("canCheckout", is(false));
//    }
}