package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class WorkPlaceMapper {

    public WorkPlaceGetDto workPlaceToDto(WorkPlace workPlace) {
        return new WorkPlaceGetDto(
            workPlace.getId(),
            workPlace.getType().getName(),
            workPlace.getFloor().getId(),
            workPlace.getCapacity()
        );
    }

    public WorkPlace dtoToWorkPlace(WorkPlaceType type, Floor floor, Integer capacity) {
        return new WorkPlace()
            .setType(type)
            .setFloor(floor)
            .setCapacity(capacity);
    }

    public WorkPlace dtoToWorkPlace(WorkPlaceUpdateDto dto, WorkPlaceType type, Floor floor) {
        return new WorkPlace()
            .setId(dto.getId())
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
            workPlace.getType().getName(),
            workPlace.getFloor().getId(),
            workPlace.getCapacity(),
            isFree);
    }

}
