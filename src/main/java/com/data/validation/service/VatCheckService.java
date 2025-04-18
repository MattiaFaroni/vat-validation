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
     * Validates a VAT number by interacting with the VIES service.
     * @param vatCheckRequest the request object containing country code and VAT number
     * @return a VatCheckResponse object containing the service data and system response
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
     * Generates a JSON request body for the VIES service
     * @param vatCheckRequest the request object containing the country code and VAT number
     * @param objectMapper the object mapper used for serializing the request into JSON
     * @return a JSON string representing the request body, or null if an error occurs during serialization
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
     * Interacts with the VIES service to validate VAT information.
     * Sends a POST request to the VIES service with the given JSON request body and processes the response.
     * @param jsonRequestBody the JSON request body to send to the VIES service
     * @param objectMapper the ObjectMapper used to parse the response from the VIES service
     * @return a VatCheckResponse object containing the service data and system response
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
     * Makes a call to the VIES service using the provided HTTP request.
     * @param request the HTTP request to be sent to the VIES service
     * @return the HTTP response as a string, or null if an exception occurs during the request
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
     * Processes the response from the VIES service to generate a VAT check response.
     * @param response the HTTP response received from the VIES service
     * @param objectMapper the ObjectMapper used to deserialize the response body into a CheckVatResponse object
     * @return a VatCheckResponse object containing the processed data and system response
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
     * Composes a VatCheckResponse object using the provided VatCheckOutputData and System instances.
     * @param vatCheckOutputData the output data containing VAT information, including name and address
     * @param system the system details including status code and description
     * @return a VatCheckResponse object containing the processed output data and system details
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
     * Composes a VatCheckResponse object using the provided System parameter.
     * @param system the system details including status code and description
     * @return a VatCheckResponse object containing the system details
     */
    private VatCheckResponse composeResponse(System system) {
        VatCheckResponse vatCheckResponse = new VatCheckResponse();
        vatCheckResponse.setSystem(system);
        return vatCheckResponse;
    }
    // spotless:on
}
