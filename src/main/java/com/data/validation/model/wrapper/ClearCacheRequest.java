package com.data.validation.model.wrapper;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClearCacheRequest {

    @Size(min = 2, max = 2)
    private String iso2;

    private String vatNumber;
}
