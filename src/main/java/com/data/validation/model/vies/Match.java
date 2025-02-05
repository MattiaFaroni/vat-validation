package com.data.validation.model.vies;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum Match {
    VALID("VALID"),
    INVALID("INVALID"),
    NOT_PROCESSED("NOT_PROCESSED");

    private String value;

    Match(String value) {
        this.value = value;
    }

    public static Match fromString(String s) {
        for (Match b : Match.values()) {
            if (java.util.Objects.toString(b.value).equals(s)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected string value: " + s);
    }

    @JsonCreator
    public static Match fromValue(String value) {
        for (Match b : Match.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
