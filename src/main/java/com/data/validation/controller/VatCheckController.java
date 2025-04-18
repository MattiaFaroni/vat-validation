package com.data.validation.controller;

import com.data.validation.api.VatCheckInterface;
import com.data.validation.listener.ApplicationListener;
import com.data.validation.logging.Logger;
import com.data.validation.model.wrapper.System;
import com.data.validation.model.wrapper.VatCheckRequest;
import com.data.validation.model.wrapper.VatCheckResponse;
import com.data.validation.redis.RedisCacheManager;
import com.data.validation.service.VatCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.Set;

@Path("/vies/check")
public class VatCheckController extends Logger implements VatCheckInterface {

    RedisCacheManager cacheManager = ApplicationListener.cacheManager;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Override
    // spotless:off
    public VatCheckResponse vatCheck(VatCheckRequest vatCheckRequest) {
        if (vatCheckRequest == null) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(generateBodyException())
                    .build());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Set<ConstraintViolation<VatCheckRequest>> constraintViolations = validator.validate(vatCheckRequest);

        if (constraintViolations.isEmpty()) {

            String cacheKey = vatCheckRequest.getIso2() + ":" + vatCheckRequest.getVatNumber();
            String cacheData = retrieveRedisData(cacheKey);

            VatCheckResponse vatCheckResponse = null;
            if (cacheData != null) {
                vatCheckResponse = readDataOnRedis(cacheData, objectMapper);
            }

            if (vatCheckResponse == null) {
                VatCheckService vatCheckService = new VatCheckService();
                vatCheckResponse = vatCheckService.vatCheck(vatCheckRequest);

                saveDataOnRedis(cacheKey, objectMapper, vatCheckResponse);
            }
            return vatCheckResponse;

        } else {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(generateBodyException())
                    .build());
        }
    }
    // spotless:on

    /**
     * Retrieves data from Redis for the given cache key.
     * @param cacheKey the key used to fetch data from the Redis cache
     * @return the retrieved data as a String if successful, or null in case of an error
     */
    private String retrieveRedisData(String cacheKey) {
        try {
            return cacheManager.getFromCache(cacheKey);
        } catch (Exception e) {
            printError("Error retrieving data from Redis", e.getMessage());
            return null;
        }
    }

    /**
     * Reads and deserializes data from a cached Redis entry.
     * @param cachedData the serialized data retrieved from the Redis cache
     * @param objectMapper the object mapper used for deserialization from JSON to a VatCheckResponse object
     * @return a VatCheckResponse object if deserialization is successful, or null if there is an error or the cached data is null
     */
    private VatCheckResponse readDataOnRedis(String cachedData, ObjectMapper objectMapper) {
        if (cachedData != null) {
            try {
                return objectMapper.readValue(cachedData, VatCheckResponse.class);
            } catch (Exception e) {
                printError("Error reading value from Redis", e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * Saves the specified VAT check response data to Redis cache under the provided cache key.
     * @param cacheKey the key under which the data will be stored in the Redis cache
     * @param objectMapper the ObjectMapper instance used to serialize the VAT check response into JSON format
     * @param vatCheckResponse the VAT check response object that needs to be stored in the Redis cache
     */
    // spotless:off
    private void saveDataOnRedis(String cacheKey, ObjectMapper objectMapper, VatCheckResponse vatCheckResponse) {
        try {
            cacheManager.putInCache(cacheKey, objectMapper.writeValueAsString(vatCheckResponse));
        } catch (Exception e) {
            printError("Error writing value to Redis", e.getMessage());
        }
    }
    // spotless:on

    /**
     * Generates a default error response when the request body is determined to be invalid.
     * @return a VatCheckResponse object containing system details with the error code set to KO and the description set to BODY_NOT_VALID
     */
    private VatCheckResponse generateBodyException() {
        System system = new System();
        system.setCode(System.CodeEnum.KO);
        system.setDescription(System.DescriptionEnum.BODY_NOT_VALID);
        VatCheckResponse vatCheckResponse = new VatCheckResponse();
        vatCheckResponse.setSystem(system);
        return vatCheckResponse;
    }
}
