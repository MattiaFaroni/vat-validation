package com.data.validation.controller;

import com.data.validation.api.VatCheckInterface;
import com.data.validation.model.wrapper.VatCheckRequest;
import com.data.validation.model.wrapper.VatCheckResponse;
import com.data.validation.service.VatCheckService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.Path;
import com.data.validation.model.wrapper.System;
import java.util.Set;

@Path("/vies/check")
public class VatCheckController implements VatCheckInterface {

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @Override
    public VatCheckResponse vatCheck(VatCheckRequest vatCheckRequest) {
        if (vatCheckRequest == null) {
            return generateBodyException();
        }

        Set<ConstraintViolation<VatCheckRequest>> constraintViolations = validator.validate(vatCheckRequest);

        if (constraintViolations.isEmpty()) {
            VatCheckService vatCheckService = new VatCheckService();
            return vatCheckService.vatCheck(vatCheckRequest);

        } else {
            return generateBodyException();
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
