package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class TeamMemberMapper {

    public TeamMemberDto memberToDto(TeamMemberEntity member) {
        var dto = new TeamMemberDto(
            member.getId(),
            member.getTeam().getId(),
            member.getEmployee().getId());
        return dto;
    }

    public TeamMemberEntity dtoToMember(
        TeamMemberDto dto, TeamEntity team, EmployeeEntity employee
    ) {
        var member = new TeamMemberEntity()
            .setId(dto.getId())
            .setTeam(team)
            .setEmployee(employee);
        return member;
    }

    public TeamMemberCreateDto createMemberToDto(TeamMemberEntity member) {
        var dto = new TeamMemberCreateDto(
            member.getTeam().getId(),
            member.getEmployee().getId());
        return dto;
    }

    public TeamMemberEntity dtoToCreateMember(
        TeamEntity team, EmployeeEntity employee
    ) {
        var member = new TeamMemberEntity()
            .setTeam(team)
            .setEmployee(employee);
        return member;
    }

}
