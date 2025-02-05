package com.data.validation.model.vies;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckVatResponse {

    private String countryCode;
    private String vatNumber;
    private OffsetDateTime requestDate;
    private Boolean valid;
    private String requestIdentifier;
    private String name;
    private String address;
    private String traderName;
    private String traderStreet;
    private String traderPostalCode;
    private String traderCity;
    private String traderCompanyType;
    private Match traderNameMatch;
    private Match traderStreetMatch;
    private Match traderPostalCodeMatch;
    private Match traderCityMatch;
    private Match traderCompanyTypeMatch;
}
