package com.data.validation.model.wrapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class System {

    private int code;
    private String description;

    public void setCode(CodeEnum code) {
        this.code = code.getValue();
    }

    public void setDescription(DescriptionEnum description) {
        this.description = description.getValue();
    }

    @Getter
    @NoArgsConstructor
    public enum CodeEnum {
        OK(0),
        KO(1);

        private Integer value;

        CodeEnum(Integer v) {
            value = v;
        }

        public static CodeEnum fromString(String s) {
            for (CodeEnum b : CodeEnum.values()) {
                if (java.util.Objects.toString(b.value).equals(s)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected string value: " + s);
        }

        @JsonCreator
        public static CodeEnum fromValue(Integer value) {
            for (CodeEnum b : CodeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }

    @Getter
    @NoArgsConstructor
    public enum DescriptionEnum {
        OK("Ok"),
        REQUEST_FAILED("Request to VIES failed"),
        BODY_CREATION_FAILED("Error creating body of VIES service request"),
        READ_RESPONSE_FAILED("Error reading VIES response"),
        BODY_NOT_VALID("Request body not valid");

        private String value;

        DescriptionEnum(String v) {
            value = v;
        }

        public static DescriptionEnum fromString(String s) {
            for (DescriptionEnum b : DescriptionEnum.values()) {
                if (java.util.Objects.toString(b.value).equals(s)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected string value: " + s);
        }

        @JsonCreator
        public static DescriptionEnum fromValue(String value) {
            for (DescriptionEnum b : DescriptionEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
}
