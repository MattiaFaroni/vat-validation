package com.data.validation.mapper;

import com.data.validation.model.vies.CheckVatResponse;
import com.data.validation.model.wrapper.VatCheckOutputData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VatCheckMapper {

    VatCheckMapper INSTANCE = Mappers.getMapper(VatCheckMapper.class);

    @Mapping(target = "iso2", source = "checkVatResponse.countryCode")
    @Mapping(target = "address", source = "checkVatResponse.address", qualifiedByName = "correctData")
    VatCheckOutputData viesToWrapper(CheckVatResponse checkVatResponse);

    @Named("correctData")
    default String correctData(String value) {
        return value.replace("\n", "");
    }
}
