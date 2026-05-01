package com.jeffersonjr.checkout.client;

import com.jeffersonjr.checkout.dto.BillingSummaryResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1/billing/customers")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "billing-api")
public interface BillingApiClient {

    @GET
    @Path("/{customerId}/summary")
    BillingSummaryResponse getBillingSummary(@PathParam("customerId") String customerId);
}