package com.jeffersonjr.checkout.client;

import com.jeffersonjr.checkout.dto.CustomerResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1/customers")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "customer-api")
public interface CustomerApiClient {

    @GET
    @Path("/{id}")
    CustomerResponse getCustomerById(@PathParam("id") String customerId);
}