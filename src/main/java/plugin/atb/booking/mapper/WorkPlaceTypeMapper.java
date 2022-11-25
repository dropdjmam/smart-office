package plugin.atb.booking.mapper;

import lombok.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
@RequiredArgsConstructor
public class WorkPlaceTypeMapper {

    public WorkPlaceTypeDto typeToDto(WorkPlaceTypeEntity workPlaceType) {

        return new WorkPlaceTypeDto(
            workPlaceType.getId(),
            workPlaceType.getName()
        );
    }

    public WorkPlaceTypeEntity dtoToType(WorkPlaceTypeDto workPlaceTypeDto) {

        return new WorkPlaceTypeEntity(
            workPlaceTypeDto.getId(),
            workPlaceTypeDto.getName()
        );
    }

}
