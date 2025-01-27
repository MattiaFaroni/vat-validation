package com.data.validation.model.vies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryStatus {

    private AvailabilityEnum availability;
    private String countryCode;

    @Getter
    public enum AvailabilityEnum {

        AVAILABLE("Available"),
        UNAVAILABLE("Unavailable"),
        MONITORING_DISABLED("Monitoring Disabled");

        private final String value;

        AvailabilityEnum(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        public static AvailabilityEnum fromString(String s) {
            for (AvailabilityEnum b : AvailabilityEnum.values()) {
                if (Objects.toString(b.value).equals(s)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected string value: " + s);
        }

        @JsonCreator
        public static AvailabilityEnum fromValue(String value) {
            for (AvailabilityEnum b : AvailabilityEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
}
