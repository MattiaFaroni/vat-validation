package com.data.validation.api;

import com.data.validation.model.wrapper.ViesStatusResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;

public interface ViesStatusInterface {

    @GET
    @Produces({"application/json"})
    ViesStatusResponse viesStatus();
}
