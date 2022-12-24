package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class OfficeMapper {

    public OfficeGetDto officeToDto(Office office) {
        return new OfficeGetDto(
            office.getId(),
            office.getCity().getName(),
            office.getAddress(),
            office.getWorkNumber(),
            office.getStartOfDay(),
            office.getEndOfDay(),
            office.getBookingRange());
    }

    public Office dtoToOffice(OfficeCreateDto dto, City city) {
        return new Office()
            .setCity(city)
            .setAddress(dto.getAddress())
            .setWorkNumber(dto.getWorkNumber())
            .setStartOfDay(dto.getStartOfDay())
            .setEndOfDay(dto.getEndOfDay())
            .setBookingRange(dto.getBookingRange());
    }

    public Office dtoToOffice(OfficeUpdateDto dto, City city) {
        return new Office()
            .setId(dto.getId())
            .setCity(city)
            .setAddress(dto.getAddress())
            .setWorkNumber(dto.getWorkNumber())
            .setStartOfDay(dto.getStartOfDay())
            .setEndOfDay(dto.getEndOfDay())
            .setBookingRange(dto.getBookingRange());
    }

}
