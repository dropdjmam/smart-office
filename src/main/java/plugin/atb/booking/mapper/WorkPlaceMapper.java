package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class WorkPlaceMapper {

    public WorkPlaceGetDto workPlaceToDto(WorkPlaceEntity workPlace) {

        var dto = new WorkPlaceGetDto(
            workPlace.getId(),
            workPlace.getType().getName(),
            workPlace.getFloor().getId(),
            workPlace.getCapacity()
        );

        return dto;

    }

    public WorkPlaceEntity dtoToWorkPlace(
        WorkPlaceTypeEntity type,
        FloorEntity floor,
        Integer capacity
    ) {
        var workPlace = new WorkPlaceEntity()
            .setType(type)
            .setFloor(floor)
            .setCapacity(capacity);

        return workPlace;

    }

    public WorkPlaceEntity dtoToWorkPlace(
        WorkPlaceUpdateDto dto,
        WorkPlaceTypeEntity type,
        FloorEntity floor
    ) {
        var workPlace = new WorkPlaceEntity()
            .setId(dto.getId())
            .setType(type)
            .setFloor(floor)
            .setCapacity(dto.getCapacity());

        return workPlace;

    }

    public PlaceAvailabilityResponseDto placeToPlaceAvailabilityDto(
        WorkPlaceEntity workPlace,
        Boolean isFree
    ) {

        var dto = new PlaceAvailabilityResponseDto(
            workPlace.getId(),
            workPlace.getType().getName(),
            workPlace.getFloor().getId(),
            workPlace.getCapacity(),
            isFree
        );

        return dto;

    }

}
