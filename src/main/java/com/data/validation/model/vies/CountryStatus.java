package com.data.validation.model.vies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryStatus {
    private String countryCode;

    @Getter
    public enum AvailabilityEnum {

        AVAILABLE(("Available")),
        UNAVAILABLE(("Unavailable")),
        MONITORING_DISABLED(("Monitoring Disabled"));

        private final String value;

        AvailabilityEnum(String v) {
            value = v;
        }
    }
}
