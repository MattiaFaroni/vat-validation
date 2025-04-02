package com.data.validation.api;

import com.data.validation.model.wrapper.ClearCacheRequest;
import com.data.validation.model.wrapper.ClearCacheResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;

public interface ClearCacheInterface {

    @DELETE
    @Consumes({"application/json"})
    @Produces({"application/json"})
    ClearCacheResponse clearCache(@Valid ClearCacheRequest clearCacheRequest);
}
