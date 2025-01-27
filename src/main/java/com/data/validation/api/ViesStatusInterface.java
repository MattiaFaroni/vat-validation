package com.data.validation.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import com.data.validation.model.wrapper.ViesStatusResponse;

public interface ViesStatusInterface {

    @GET
    @Produces({"application/json"})
    ViesStatusResponse viesStatus();
}
