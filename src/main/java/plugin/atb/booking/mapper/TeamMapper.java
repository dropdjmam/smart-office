package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class TeamMapper {

    public TeamDto teamToDto(Team team) {
        return new TeamDto(
            team.getId(),
            team.getLeader().getId(),
            team.getName()
        );
    }

    public TeamGetDto teamToGetDto(Team team) {
        return new TeamGetDto(
            team.getId(),
            team.getLeader().getId(),
            team.getLeader().getFullName(),
            team.getName()
        );
    }

    public Team dtoToTeam(TeamDto dto, Employee employee) {
        return new Team()
            .setId(dto.getId())
            .setLeader(employee)
            .setName(dto.getName());
    }

    public Team dtoToCreateTeam(TeamCreateDto dto, Employee employee) {
        return new Team()
            .setLeader(employee)
            .setName(dto.getName());
    }

}
