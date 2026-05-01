package com.jeffersonjr.checkout.resource;

import com.jeffersonjr.checkout.dto.CheckoutSummaryResponse;
import com.jeffersonjr.checkout.service.CheckoutService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/v1/checkouts")
@Produces(MediaType.APPLICATION_JSON)
public class CheckoutResource {

    @Inject
    CheckoutService checkoutService;

    @GET
    @Path("/{customerId}/summary")
    public CheckoutSummaryResponse getCheckoutSummary(@PathParam("customerId") String customerId) {
        return checkoutService.getCheckoutSummary(customerId);
    }
}