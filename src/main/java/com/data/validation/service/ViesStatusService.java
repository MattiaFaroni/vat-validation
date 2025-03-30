package com.data.validation.service;

import com.data.validation.listener.ApplicationListener;
import com.data.validation.logging.Logger;
import com.data.validation.mapper.ViesStatusMapper;
import com.data.validation.model.vies.StatusInformationResponse;
import com.data.validation.model.wrapper.System;
import com.data.validation.model.wrapper.ViesStatusResponse;
import com.google.gson.Gson;
import io.sentry.Sentry;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ViesStatusService extends Logger {

    /**
     * Method used to check the status of the VIES service
     * @return service response
     */
    public ViesStatusResponse checkStatus() {
        StatusInformationResponse statusInformationResponse = callViesService();
        ViesStatusResponse viesStatusResponse = new ViesStatusResponse();
        System system = new System();

        if (statusInformationResponse != null) {
            viesStatusResponse = ViesStatusMapper.INSTANCE.viesToWrapper(statusInformationResponse);
            system.setCode(System.CodeEnum.OK);
            system.setDescription(System.DescriptionEnum.OK);

            viesStatusResponse.setSystem(system);
            return viesStatusResponse;

        } else {
            system.setCode(System.CodeEnum.KO);
            system.setDescription(System.DescriptionEnum.REQUEST_FAILED);

            viesStatusResponse.setSystem(system);
            throw new ServiceUnavailableException(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(viesStatusResponse)
                    .build());
        }
    }

    /**
     * Method used to call the VIES service to check the status
     * @return VIES service status information
     */
    // spotless:off
    private StatusInformationResponse callViesService() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = ApplicationListener.configuration.getParameters().getVies().getCheckStatusUrl();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), StatusInformationResponse.class);

        } catch (Exception e) {
            Sentry.captureException(e);
            printError("Error while calling the VIES status service", e.getMessage());
            return null;
        }
    }
    // spotless:on
}
