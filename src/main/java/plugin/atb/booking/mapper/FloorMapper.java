package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class FloorMapper {

    public FloorGetDto floorToDto(Floor floor) {

        var image = floor.getMapFloor();
        Long floorId = null;
        if (image != null) {
            floorId = image.getId();
        }

        return new FloorGetDto(
            floor.getId(),
            floor.getOffice().getId(),
            floor.getFloorNumber(),
            floorId);
    }

    public Floor dtoToFloor(FloorUpdateDto dto, Office office) {
        return new Floor()
            .setId(dto.getId())
            .setOffice(office)
            .setFloorNumber(dto.getFloorNumber());
    }

    public Floor dtoToFloor(FloorCreateDto dto, Office office) {
        return new Floor()
            .setOffice(office)
            .setFloorNumber(dto.getFloorNumber());
    }

}


