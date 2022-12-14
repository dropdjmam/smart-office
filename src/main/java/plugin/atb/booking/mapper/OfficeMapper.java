package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class OfficeMapper {

    public OfficeGetDto officeToDto(OfficeEntity office) {
        var dto = new OfficeGetDto(
            office.getId(),
            office.getCity().getName(),
            office.getAddress(),
            office.getWorkNumber(),
            office.getStartOfDay(),
            office.getEndOfDay(),
            office.getBookingRange());

        return dto;
    }

    public OfficeEntity dtoToOffice(OfficeCreateDto dto, CityEntity city) {
        var office = new OfficeEntity()
            .setCity(city)
            .setAddress(dto.getAddress())
            .setWorkNumber(dto.getWorkNumber())
            .setStartOfDay(dto.getStartOfDay())
            .setEndOfDay(dto.getEndOfDay())
            .setBookingRange(dto.getBookingRange());

        return office;
    }

    public OfficeEntity dtoToOffice(OfficeUpdateDto dto, CityEntity city) {
        var office = new OfficeEntity()
            .setId(dto.getId())
            .setCity(city)
            .setAddress(dto.getAddress())
            .setWorkNumber(dto.getWorkNumber())
            .setStartOfDay(dto.getStartOfDay())
            .setEndOfDay(dto.getEndOfDay())
            .setBookingRange(dto.getBookingRange());

        return office;
    }

}
