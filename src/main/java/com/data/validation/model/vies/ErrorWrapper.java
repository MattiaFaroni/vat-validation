package com.data.validation.model.vies;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorWrapper {

    private String error;
    private String message;
}
