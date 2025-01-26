package com.data.validation.model.vies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckVatRequest {

    private String countryCode;
    private String vatNumber;
    private String requesterMemberStateCode;
    private String requesterNumber;
    private String traderName;
    private String traderStreet;
    private String traderPostalCode;
    private String traderCity;
    private String traderCompanyType;
}
