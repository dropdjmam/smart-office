package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class ConferenceMemberMapper {

    public ConferenceMemberDto conferenceMemberToDto(ConferenceMember member) {
        return new ConferenceMemberDto(
            member.getId(),
            member.getEmployee().getId(),
            member.getBooking().getId());
    }

    public ConferenceMember dtoToConferenceMember(
        ConferenceMemberDto dto, Employee employee, Booking booking
    ) {
        return new ConferenceMember()
            .setId(dto.getId())
            .setEmployee(employee)
            .setBooking(booking);
    }

    public ConferenceMemberCreateDto createConferenceMemberToDto(ConferenceMember member) {
        return new ConferenceMemberCreateDto(
            member.getEmployee().getId(),
            member.getBooking().getId());
    }

    public ConferenceMember dtoToCreateConferenceMember(Employee employee, Booking booking) {
        return new ConferenceMember()
            .setEmployee(employee)
            .setBooking(booking);
    }

}
