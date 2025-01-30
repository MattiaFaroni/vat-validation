package com.data.validation.controller;

import com.data.validation.api.VatCheckInterface;
import com.data.validation.listener.ApplicationListener;
import com.data.validation.logging.Logger;
import com.data.validation.model.wrapper.VatCheckRequest;
import com.data.validation.model.wrapper.VatCheckResponse;
import com.data.validation.redis.RedisCacheManager;
import com.data.validation.service.VatCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.Path;
import com.data.validation.model.wrapper.System;
import java.util.Set;

@Path("/vies/check")
public class VatCheckController extends Logger implements VatCheckInterface {

    RedisCacheManager cacheManager = ApplicationListener.cacheManager;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Override
    public VatCheckResponse vatCheck(VatCheckRequest vatCheckRequest) {
        if (vatCheckRequest == null) {
            return generateBodyException();
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
            return generateBodyException();
        }
    }

    /**
     * Method used to retrieve data from Redis
     * @param cacheKey Redis key
     * @return Redis data
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
     * Method used to read data stored on Redis
     * @param cachedData Redis data
     * @param objectMapper java object serializer/deserializer
     * @return service response
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
     * Method used to save the request inside Redis
     * @param cacheKey Redis key
     * @param objectMapper java object serializer/deserializer
     * @param vatCheckResponse service response
     */
    private void saveDataOnRedis(String cacheKey, ObjectMapper objectMapper, VatCheckResponse vatCheckResponse) {
        try {
            cacheManager.putInCache(cacheKey, objectMapper.writeValueAsString(vatCheckResponse));
        } catch (Exception e) {
            printError("Error writing value to Redis", e.getMessage());
        }
    }

    /**
     * Method used to generate the service response in case of invalid body
     * @return service response
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
