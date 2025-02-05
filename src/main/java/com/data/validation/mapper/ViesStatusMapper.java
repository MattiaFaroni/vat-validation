package com.data.validation.mapper;

import com.data.validation.model.vies.CountryStatus;
import com.data.validation.model.vies.StatusInformationResponse;
import com.data.validation.model.wrapper.IsoAvailable;
import com.data.validation.model.wrapper.ViesStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ViesStatusMapper {

    ViesStatusMapper INSTANCE = Mappers.getMapper(ViesStatusMapper.class);

    @Mapping(target = "system", ignore = true)
    @Mapping(target = "data", source = "statusInformationResponse.countries")
    ViesStatusResponse viesToWrapper(StatusInformationResponse statusInformationResponse);

    // spotless:off
    @Mapping(target = "iso2", source = "countryStatus.countryCode")
    @Mapping(target = "status", expression = "java(countryStatus.getAvailability() != null ? countryStatus.getAvailability().toString() : null)")
    IsoAvailable viesToWrapper(CountryStatus countryStatus);
    // spotless:on
}
