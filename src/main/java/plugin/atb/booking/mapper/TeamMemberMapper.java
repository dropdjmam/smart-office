package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class TeamMemberMapper {

    public TeamMemberDto teamMemberToDto(TeamMemberEntity member) {
        var dto = new TeamMemberDto(
            member.getId(),
            member.getTeam().getId(),
            member.getEmployee().getId());
        return dto;
    }

    public TeamMemberInfoDto teamMemberToInfoDto(TeamMemberEntity member) {
        var dto = new TeamMemberInfoDto(
            member.getId(),
            member.getTeam().getId(),
            member.getEmployee().getId(),
            member.getEmployee().getFullName(),
            member.getEmployee().getRole().getName());
        return dto;
    }

    public TeamMemberInfoTeamDto teamMemberToInfoTeamDto(TeamMemberEntity member) {
        var dto = new TeamMemberInfoTeamDto(
            member.getTeam().getId(),
            member.getTeam().getLeader().getId(),
            member.getTeam().getLeader().getFullName(),
            member.getTeam().getName());
        return dto;
    }

    public TeamMemberEntity dtoToTeamMember(
        TeamMemberDto dto, TeamEntity team, EmployeeEntity employee
    ) {
        var member = new TeamMemberEntity()
            .setId(dto.getId())
            .setTeam(team)
            .setEmployee(employee);
        return member;
    }

    public TeamMemberEntity dtoToCreateTeamMember(
        TeamEntity team, EmployeeEntity employee
    ) {
        var member = new TeamMemberEntity()
            .setTeam(team)
            .setEmployee(employee);
        return member;
    }

}
