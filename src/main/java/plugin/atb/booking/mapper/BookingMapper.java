package plugin.atb.booking.mapper;

import static java.time.ZoneOffset.*;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class BookingMapper {

    public Booking dtoToBooking(
        BookingCreateDto dto,
        Employee holder,
        Employee maker,
        WorkPlace workPlace
    ) {
        return new Booking()
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart().withZoneSameInstant(UTC).toLocalDateTime())
            .setDateTimeOfEnd(dto.getEnd().withZoneSameInstant(UTC).toLocalDateTime())
            .setGuests(dto.getGuests())
            .setIsDeleted(false);
    }

    public Booking dtoToBooking(
        BookingGroupCreateDto dto,
        Employee holder,
        Employee maker,
        WorkPlace workPlace
    ) {
        return new Booking()
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart().withZoneSameInstant(UTC).toLocalDateTime())
            .setDateTimeOfEnd(dto.getEnd().withZoneSameInstant(UTC).toLocalDateTime())
            .setGuests(dto.getGuests())
            .setIsDeleted(false);
    }

    public Booking dtoToBooking(
        BookingUpdateDto dto,
        Employee holder,
        Employee maker,
        WorkPlace workPlace
    ) {
        return new Booking()
            .setId(dto.getId())
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart().withZoneSameInstant(UTC).toLocalDateTime())
            .setDateTimeOfEnd(dto.getEnd().withZoneSameInstant(UTC).toLocalDateTime())
            .setGuests(dto.getGuests())
            .setIsDeleted(false);
    }

}
