package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class WorkPlaceTypeMapper {

    public WorkPlaceTypeDto typeToDto(WorkPlaceType type) {
        return new WorkPlaceTypeDto(
            type.getId(),
            type.getName());
    }

    public WorkPlaceType dtoToType(WorkPlaceTypeDto dto) {
        return new WorkPlaceType()
            .setId(dto.getId())
            .setName(dto.getName());
    }

    public WorkPlaceType createDtoToType(WorkPlaceTypeCreateDto dto) {
        return new WorkPlaceType()
            .setName(dto.getName());
    }

}
