package plugin.atb.booking.mapper;

import lombok.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
@RequiredArgsConstructor
public class FloorMapper {

    public FloorDto floorToDto(FloorEntity floor) {
        var dto = new FloorDto(
            floor.getId(),
            floor.getOffice().getId(),
            floor.getFloorNumber(),
            floor.getMapFloor()
        );

        return dto;
    }

    public FloorEntity dtoToFloor(FloorDto dto, OfficeEntity office) {
        var floor = new FloorEntity(
            dto.getId(),
            office,
            dto.getFloorNumber(),
            dto.getMapFloor()
        );

        return floor;
    }

    public FloorEntity dtoToFloor(FloorCreateDto dto, OfficeEntity office) {
        var floor = new FloorEntity()
            .setOffice(office)
            .setFloorNumber(dto.getFloorNumber())
            .setMapFloor(dto.getMapFloor()
            );

        return floor;
    }

}


