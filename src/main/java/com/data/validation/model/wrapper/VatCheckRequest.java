package com.data.validation.model.wrapper;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VatCheckRequest {

    @NotNull
    @Size(min = 2, max = 2)
    private String iso2;

    @NotNull
    private String vatNumber;
}
