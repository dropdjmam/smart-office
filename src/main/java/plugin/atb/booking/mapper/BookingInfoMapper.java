package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class BookingInfoMapper {

    public BookingPartInfoDto bookingToDto(BookingEntity booking) {
        var dto = new BookingPartInfoDto(
            booking.getId(),
            booking.getMaker().getId(),
            booking.getMaker().getFullName(),
            booking.getHolder().getId(),
            booking.getHolder().getFullName(),
            booking.getDateTimeOfStart(),
            booking.getDateTimeOfEnd()
        );

        return dto;
    }

    public BookingInfoPlaceDto placeToDto(WorkPlaceEntity workPlace) {
        var dto = new BookingInfoPlaceDto(
            workPlace.getId(),
            workPlace.getType().getName(),
            workPlace.getFloor().getFloorNumber()
        );

        return dto;
    }

    public BookingInfoOfficeDto officeToDto(OfficeEntity office) {
        var dto = new BookingInfoOfficeDto(
            office.getId(),
            office.getCity().getName(),
            office.getAddress()
        );

        return dto;
    }

}
