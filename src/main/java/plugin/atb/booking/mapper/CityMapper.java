package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class CityMapper {

    public CityDto cityToDto(City city) {
        return new CityDto(city.getId(), city.getName(), city.getZoneId());
    }

    public City dtoToCity(CityDto dto) {
        return new City(dto.getId(), dto.getName(), dto.getZoneId());
    }

}
