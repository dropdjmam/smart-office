package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class ConferenceMemberMapper {

    public ConferenceMemberDto conferenceMemberToDto(ConferenceMemberEntity member) {
        var dto = new ConferenceMemberDto(
            member.getId(),
            member.getEmployee().getId(),
            member.getBooking().getId());
        return dto;
    }

    public ConferenceMemberEntity dtoToConferenceMember(
        ConferenceMemberDto dto, EmployeeEntity employee, BookingEntity booking
    ) {
        var member = new ConferenceMemberEntity()
            .setId(dto.getId())
            .setEmployee(employee)
            .setBooking(booking);
        return member;
    }

    public ConferenceMemberCreateDto createConferenceMemberToDto(ConferenceMemberEntity member) {
        var dto = new ConferenceMemberCreateDto(
            member.getEmployee().getId(),
            member.getBooking().getId());
        return dto;
    }

    public ConferenceMemberEntity dtoToCreateConferenceMember(
        EmployeeEntity employee, BookingEntity booking
    ) {
        var member = new ConferenceMemberEntity()
            .setEmployee(employee)
            .setBooking(booking);
        return member;
    }

}
