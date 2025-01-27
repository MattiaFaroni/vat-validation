package com.data.validation.controller;

import com.data.validation.api.ViesStatusInterface;
import com.data.validation.model.wrapper.ViesStatusResponse;
import com.data.validation.service.ViesStatusService;
import jakarta.ws.rs.Path;

@Path("/vies/status")
public class ViesStatusController implements ViesStatusInterface {

    @Override
    public ViesStatusResponse viesStatus() {
        ViesStatusService viesStatusService = new ViesStatusService();
        return viesStatusService.checkStatus();
    }
}
