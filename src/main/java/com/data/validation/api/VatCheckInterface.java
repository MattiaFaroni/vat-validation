package com.data.validation.api;

import com.data.validation.model.wrapper.VatCheckRequest;
import com.data.validation.model.wrapper.VatCheckResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;

public interface VatCheckInterface {

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    VatCheckResponse vatCheck(@Valid VatCheckRequest vatCheckRequest);
}
