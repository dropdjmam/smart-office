package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class WorkPlaceTypeMapper {

    public WorkPlaceTypeDto typeToDto(WorkPlaceTypeEntity type) {
        var dto = new WorkPlaceTypeDto(
            type.getId(),
            type.getName());
        return dto;
    }

    public WorkPlaceTypeEntity dtoToType(WorkPlaceTypeDto dto) {
        var type = new WorkPlaceTypeEntity()
            .setId(dto.getId())
            .setName(dto.getName());
        return type;
    }

    public WorkPlaceTypeEntity createDtoToType(WorkPlaceTypeCreateDto dto) {
        var type = new WorkPlaceTypeEntity()
            .setName(dto.getName());
        return type;
    }

}
