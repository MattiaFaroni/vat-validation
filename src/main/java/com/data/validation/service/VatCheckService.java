package com.data.validation.service;

import com.data.validation.listener.ApplicationListener;
import com.data.validation.logging.Logger;
import com.data.validation.mapper.VatCheckMapper;
import com.data.validation.model.vies.CheckVatRequest;
import com.data.validation.model.vies.CheckVatResponse;
import com.data.validation.model.wrapper.System;
import com.data.validation.model.wrapper.VatCheckOutputData;
import com.data.validation.model.wrapper.VatCheckRequest;
import com.data.validation.model.wrapper.VatCheckResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.sentry.Sentry;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VatCheckService extends Logger {

    /**
     * Method used to validate a VAT number
     * @param vatCheckRequest service request
     * @return service response
     */
    // spotless:off
    public VatCheckResponse vatCheck(VatCheckRequest vatCheckRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String jsonRequestBody = generateRequestBody(vatCheckRequest, objectMapper);

        if (jsonRequestBody != null) {
            return interactWithViesService(jsonRequestBody, objectMapper);

        } else {
            System system = new System();
            system.setCode(System.CodeEnum.KO);
            system.setDescription(System.DescriptionEnum.BODY_CREATION_FAILED);

            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(composeResponse(system))
                    .build());
        }
    }

    /**
     * Method used to generate the body to query the VIES service
     * @param vatCheckRequest service request
     * @param objectMapper java class serializer/deserializer
     * @return request body
     */
    private String generateRequestBody(VatCheckRequest vatCheckRequest, ObjectMapper objectMapper) {
        CheckVatRequest checkVatRequest = new CheckVatRequest();
        checkVatRequest.setCountryCode(vatCheckRequest.getIso2());
        checkVatRequest.setVatNumber(vatCheckRequest.getVatNumber());

        try {
            return objectMapper.writeValueAsString(checkVatRequest);
        } catch (JsonProcessingException e) {
            Sentry.captureException(e);
            printError("Error creating body of VIES service request", e.getMessage());
            return null;
        }
    }

    /**
     * Method used to interact with the VIES service
     * @param jsonRequestBody VIES service body request
     * @param objectMapper java class serializer/deserializer
     * @return service response
     */
    private VatCheckResponse interactWithViesService(String jsonRequestBody, ObjectMapper objectMapper) {
        String url = ApplicationListener.configuration.getParameters().getVies().getCheckVatNumberUrl();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .build();

        HttpResponse<String> response = callViesService(request);

        if (response != null && response.statusCode() == 200) {
            return processViesResponse(response, objectMapper);

        } else {
            System system = new System();
            system.setCode(System.CodeEnum.KO);
            system.setDescription(System.DescriptionEnum.REQUEST_FAILED);

            throw new ServiceUnavailableException(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(composeResponse(system))
                    .build());
        }
    }

    /**
     * Method used to query the VIES service
     * @param request VIES service request
     * @return VIES service response
     */
    private HttpResponse<String> callViesService(HttpRequest request) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            return client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            Sentry.captureException(e);
            printError("Error while calling the VIES service", e.getMessage());
            return null;
        }

    }

    /**
     * Method used to process the VIES service response
     * @param response VIES service response
     * @param objectMapper java class serializer/deserializer
     * @return service response
     */
    private VatCheckResponse processViesResponse(HttpResponse<String> response, ObjectMapper objectMapper) {
        try {
            CheckVatResponse checkVatResponse = objectMapper.readValue(response.body(), CheckVatResponse.class);
            VatCheckOutputData vatCheckOutputData = VatCheckMapper.INSTANCE.viesToWrapper(checkVatResponse);
            System system = new System();
            system.setCode(System.CodeEnum.OK);
            system.setDescription(System.DescriptionEnum.OK);
            return composeResponse(vatCheckOutputData, system);

        } catch (JsonProcessingException e) {
            Sentry.captureException(e);
            printError("Error while processing VIES service response", e.getMessage());
            System system = new System();
            system.setCode(System.CodeEnum.KO);
            system.setDescription(System.DescriptionEnum.READ_RESPONSE_FAILED);

            throw new InternalServerErrorException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(composeResponse(system))
                    .build());
        }
    }

    /**
     * Method used to compose the service response
     * @param vatCheckOutputData service data area
     * @param system service system area
     * @return service response
     */
    private VatCheckResponse composeResponse(VatCheckOutputData vatCheckOutputData, System system) {
        if (vatCheckOutputData.getName().equals("---")) {
            vatCheckOutputData.setName("");
        }
        if (vatCheckOutputData.getAddress().equals("---")) {
            vatCheckOutputData.setAddress("");
        }
        VatCheckResponse vatCheckResponse = new VatCheckResponse();
        vatCheckResponse.setData(vatCheckOutputData);
        vatCheckResponse.setSystem(system);
        return vatCheckResponse;
    }

    /**
     * Method used to compose the service response
     * @param system service system area
     * @return service response
     */
    private VatCheckResponse composeResponse(System system) {
        VatCheckResponse vatCheckResponse = new VatCheckResponse();
        vatCheckResponse.setSystem(system);
        return vatCheckResponse;
    }
    // spotless:on
}
