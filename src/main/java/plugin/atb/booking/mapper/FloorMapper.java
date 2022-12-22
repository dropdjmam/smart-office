package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class FloorMapper {

    public FloorGetDto floorToDto(FloorEntity floor) {

        var image = floor.getMapFloor();
        Long floorId = null;
        if (image != null) {
            floorId = image.getId();
        }

        var dto = new FloorGetDto(
            floor.getId(),
            floor.getOffice().getId(),
            floor.getFloorNumber(),
            floorId
        );

        return dto;
    }

    public FloorEntity dtoToFloor(FloorUpdateDto dto, OfficeEntity office) {
        var floor = new FloorEntity()
            .setId(dto.getId())
            .setOffice(office)
            .setFloorNumber(dto.getFloorNumber());

        return floor;
    }

    public FloorEntity dtoToFloor(FloorCreateDto dto, OfficeEntity office) {
        var floor = new FloorEntity()
            .setOffice(office)
            .setFloorNumber(dto.getFloorNumber());

        return floor;
    }

}


