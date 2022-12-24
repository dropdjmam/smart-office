package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class TeamMemberMapper {

    public TeamMemberDto teamMemberToDto(TeamMember member) {
        return new TeamMemberDto(
            member.getId(),
            member.getTeam().getId(),
            member.getEmployee().getId());
    }

    public TeamMemberInfoDto teamMemberToInfoDto(TeamMember member) {
        return new TeamMemberInfoDto(
            member.getId(),
            member.getTeam().getId(),
            member.getEmployee().getId(),
            member.getEmployee().getFullName(),
            member.getEmployee().getRole().getName());
    }

    public TeamMemberInfoTeamDto teamMemberToInfoTeamDto(TeamMember member) {
        return new TeamMemberInfoTeamDto(
            member.getTeam().getId(),
            member.getTeam().getLeader().getId(),
            member.getTeam().getLeader().getFullName(),
            member.getTeam().getName());
    }

    public TeamMember dtoToTeamMember(TeamMemberDto dto, Team team, Employee employee) {
        return new TeamMember()
            .setId(dto.getId())
            .setTeam(team)
            .setEmployee(employee);
    }

    public TeamMember dtoToCreateTeamMember(Team team, Employee employee) {
        return new TeamMember()
            .setTeam(team)
            .setEmployee(employee);
    }

}
