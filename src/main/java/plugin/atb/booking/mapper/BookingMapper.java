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
        return new BookingEntity()
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart())
            .setDateTimeOfEnd(dto.getEnd())
            .setGuests(dto.getGuests())
            .setIsDeleted(false);
    }

    public BookingEntity dtoToBooking(
        BookingGroupCreateDto dto,
        EmployeeEntity holder,
        EmployeeEntity maker,
        WorkPlaceEntity workPlace
    ) {
        return new BookingEntity()
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart())
            .setDateTimeOfEnd(dto.getEnd())
            .setGuests(dto.getGuests())
            .setIsDeleted(false);
    }

    public BookingEntity dtoToBooking(
        BookingUpdateDto dto,
        EmployeeEntity holder,
        EmployeeEntity maker,
        WorkPlaceEntity workPlace
    ) {
        return new BookingEntity()
            .setId(dto.getId())
            .setHolder(holder)
            .setMaker(maker)
            .setWorkPlace(workPlace)
            .setDateTimeOfStart(dto.getStart())
            .setDateTimeOfEnd(dto.getEnd())
            .setGuests(dto.getGuests())
            .setIsDeleted(false);
    }

}
