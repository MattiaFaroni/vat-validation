package com.data.validation.model.vies;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {

    private Boolean actionSucceed;
    private @Valid List<@Valid ErrorWrapper> errorWrappers;
}
