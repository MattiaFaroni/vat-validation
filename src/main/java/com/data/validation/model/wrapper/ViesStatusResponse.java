package com.data.validation.model.wrapper;

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
public class ViesStatusResponse {

    private @Valid List<@Valid IsoAvailable> data;
    private System system;
}
