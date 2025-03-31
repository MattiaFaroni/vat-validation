package com.data.validation.model.wrapper;

import com.fasterxml.jackson.annotation.*;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClearCacheResponse {

    private StatusEnum status;
    private MessageEnum message;

    @Getter
    @NoArgsConstructor
    public enum StatusEnum {
        SUCCESS("SUCCESS"),
        ERROR("ERROR");

        private String value;

        StatusEnum(String v) {
            value = v;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        public static StatusEnum fromString(String s) {
            for (StatusEnum b : StatusEnum.values()) {
                if (Objects.toString(b.value).equals(s)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected string value '" + s + "'");
        }

        @JsonCreator
        public static StatusEnum fromValue(String value) {
            for (StatusEnum b : StatusEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    @Getter
    @NoArgsConstructor
    public enum MessageEnum {
        CACHE_CLEARED_SUCCESSFULLY("Cache cleared successfully"),
        INCORRECT_INPUT_PARAMETERS("Incorrect input parameters"),
        REDIS_EXCEPTION("Redis exception");

        private String value;

        MessageEnum(String v) {
            value = v;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        public static MessageEnum fromString(String s) {
            for (MessageEnum b : MessageEnum.values()) {
                if (Objects.toString(b.value).equals(s)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected string value '" + s + "'");
        }

        @JsonCreator
        public static MessageEnum fromValue(String value) {
            for (MessageEnum b : MessageEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
