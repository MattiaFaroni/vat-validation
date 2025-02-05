package com.data.validation.model.vies;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {

    private Boolean actionSucceed;
    private @Valid List<@Valid ErrorWrapper> errorWrappers;
}
