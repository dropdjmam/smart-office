package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public void add(TeamMemberEntity team) {

        boolean exists = teamMemberRepository.existsByEmployeeAndTeam(
            team.getEmployee(), team.getTeam());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Участник уже состоит в данной команде: %s, %s",
                team.getEmployee().getFullName(), team.getTeam().getName()));
        }

        teamMemberRepository.save(team);
    }

    public Page<TeamMemberEntity> getAll(Pageable pageable) {

        return teamMemberRepository.findAll(pageable);
    }

    public TeamMemberEntity getByEmployeeAndTeam(
        EmployeeEntity employee, TeamEntity team
    ) {

        return teamMemberRepository.findTeamMemberByEmployeeAndTeam(employee, team);
    }

    public Page<TeamMemberEntity> getAllTeamMemberByTeamId(Long teamId, Pageable pageable) {

        return teamMemberRepository.findAllTeamMemberByTeamId(teamId, pageable);
    }

    public Page<TeamMemberEntity> getAllTeamMemberByTeamName(String name, Pageable pageable) {

        return teamMemberRepository.findAllTeamMemberByTeamName(name, pageable);
    }

    public Page<TeamMemberEntity> getAllTeamByEmployeeId(Long employeeId, Pageable pageable) {
        return teamMemberRepository.findAllTeamByEmployeeId(employeeId, pageable);
    }

    public TeamMemberEntity getById(Long id) {
        return teamMemberRepository.findById(id).orElse(null);
    }

    public Page<TeamMemberEntity> getByTeam(TeamEntity team, Pageable pageable) {
        return teamMemberRepository.findByTeam(team, pageable);
    }

    public void update(TeamMemberEntity team) {
        TeamMemberEntity updateTeamMember = getById(team.getId());

        if (updateTeamMember == null) {
            throw new NotFoundException(String.format(
                "Участник команды не найден: %s", team.getId()));
        }

        teamMemberRepository.save(team);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format("Участник команды не найден: %s", id));
        }

        teamMemberRepository.deleteById(id);
    }

    public void delete(TeamMemberEntity teamMember) {

        if (teamMember == null) {
            throw new IncorrectArgumentException("Участник команды не указан");
        }

        teamMemberRepository.delete(teamMember);
    }

}
