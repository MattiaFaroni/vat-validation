package com.data.validation.model.vies;

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
}
