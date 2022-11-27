package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class BookingMapper {

    public BookingEntity dtoToEntity(
        BookingCreateDto dto,
        EmployeeEntity holder,
        EmployeeEntity maker,
        WorkPlaceEntity workPlace
    ) {
        BookingEntity booking = new BookingEntity()
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart())
            .setDateTimeOfEnd(dto.getEnd())
            .setGuests(dto.getGuests());

        return booking;
    }

    public BookingEntity dtoToEntity(
        BookingUpdateDto dto,
        EmployeeEntity maker,
        WorkPlaceEntity workPlace
    ) {

        BookingEntity booking = new BookingEntity()
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getNewStart())
            .setDateTimeOfEnd(dto.getNewEnd())
            .setGuests(dto.getGuests());

        return booking;
    }

    public BookingGetDto bookingToDto(BookingEntity booking) {

        BookingGetDto dto = new BookingGetDto(
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
