package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class TeamMapper {

    public TeamDto teamToDto(TeamEntity team) {
        var dto = new TeamDto(
            team.getId(),
            team.getLeader().getId(),
            team.getName()
        );
        return dto;
    }

    public TeamGetDto teamToGetDto(TeamEntity team) {
        var dto = new TeamGetDto(
            team.getId(),
            team.getLeader().getId(),
            team.getLeader().getFullName(),
            team.getName()
        );
        return dto;
    }

    public TeamEntity dtoToTeam(TeamDto dto, EmployeeEntity employee) {
        var team = new TeamEntity()
            .setId(dto.getId())
            .setLeader(employee)
            .setName(dto.getName());
        return team;
    }

    public TeamEntity dtoToCreateTeam(TeamCreateDto dto, EmployeeEntity employee) {
        var team = new TeamEntity()
            .setLeader(employee)
            .setName(dto.getName());
        return team;
    }

}
