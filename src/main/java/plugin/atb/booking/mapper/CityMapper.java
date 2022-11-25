package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component

public class CityMapper {

    public CityDto cityToDto(CityEntity city) {
        return new CityDto(city.getId(), city.getName());
    }

    public CityEntity dtoToCity(CityDto dto) {
        return new CityEntity(dto.getId(), dto.getName());
    }

}
