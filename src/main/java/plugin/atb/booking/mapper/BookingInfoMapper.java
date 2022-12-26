package plugin.atb.booking.mapper;

import java.time.*;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class BookingInfoMapper {

    public InfoBookingDto bookingToDto(Booking booking, ZoneId zoneId) {
        return new InfoBookingDto(
            booking.getId(),
            booking.getMaker().getId(),
            booking.getMaker().getFullName(),
            booking.getHolder().getId(),
            booking.getHolder().getFullName(),
            booking.getDateTimeOfStart().atZone(zoneId).toLocalDateTime(),
            booking.getDateTimeOfEnd().atZone(zoneId).toLocalDateTime(),
            booking.getGuests(),
            booking.getIsDeleted()
        );
    }

    public InfoPlaceDto placeToDto(WorkPlace workPlace) {
        return new InfoPlaceDto(
            workPlace.getId(),
            workPlace.getType().getName(),
            workPlace.getFloor().getFloorNumber()
        );
    }

    public InfoOfficeDto officeToDto(Office office) {
        return new InfoOfficeDto(
            office.getId(),
            office.getCity().getName(),
            office.getCity().getZoneId(),
            office.getAddress()
        );
    }

}
