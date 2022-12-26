package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class WorkPlaceMapper {

    public WorkPlaceGetDto workPlaceToDto(WorkPlace workPlace) {
        return new WorkPlaceGetDto(
            workPlace.getId(),
            workPlace.getPlaceName(),
            workPlace.getType().getName(),
            workPlace.getFloor().getId(),
            workPlace.getCapacity()
        );
    }

    public WorkPlace dtoToWorkPlace(
        WorkPlaceCreateDto dto,
        WorkPlaceType type,
        Floor floor
    ) {
        return new WorkPlace()
            .setPlaceName(dto.getPlaceName())
            .setType(type)
            .setFloor(floor)
            .setCapacity(dto.getCapacity());
    }

    public WorkPlace dtoToWorkPlace(WorkPlaceUpdateDto dto, WorkPlaceType type, Floor floor) {
        return new WorkPlace()
            .setId(dto.getId())
            .setPlaceName(dto.getName())
            .setType(type)
            .setFloor(floor)
            .setCapacity(dto.getCapacity());
    }

    public PlaceAvailabilityResponseDto placeToPlaceAvailabilityDto(
        WorkPlace workPlace,
        Boolean isFree
    ) {
        return new PlaceAvailabilityResponseDto(
            workPlace.getId(),
            workPlace.getPlaceName(),
            workPlace.getType().getName(),
            workPlace.getFloor().getId(),
            workPlace.getCapacity(),
            isFree);
    }

}
