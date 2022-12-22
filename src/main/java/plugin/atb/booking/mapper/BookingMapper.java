package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class BookingMapper {

    public BookingEntity dtoToBooking(
        BookingCreateDto dto,
        EmployeeEntity holder,
        EmployeeEntity maker,
        WorkPlaceEntity workPlace
    ) {
        var booking = new BookingEntity()
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart())
            .setDateTimeOfEnd(dto.getEnd())
            .setGuests(dto.getGuests())
            .setIsDeleted(false);

        return booking;
    }

    public BookingEntity dtoToBooking(
        BookingUpdateDto dto,
        EmployeeEntity holder,
        EmployeeEntity maker,
        WorkPlaceEntity workPlace
    ) {
        var booking = new BookingEntity()
            .setId(dto.getId())
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart())
            .setDateTimeOfEnd(dto.getEnd())
            .setGuests(dto.getGuests())
            .setIsDeleted(false);

        return booking;
    }

    public BookingGetDto bookingToDto(BookingEntity booking) {

        var dto = new BookingGetDto(
            booking.getId(),
            booking.getHolder().getId(),
            booking.getMaker().getId(),
            booking.getWorkPlace().getId(),
            booking.getDateTimeOfStart(),
            booking.getDateTimeOfEnd(),
            booking.getGuests()
        );

        return dto;

    }

}
