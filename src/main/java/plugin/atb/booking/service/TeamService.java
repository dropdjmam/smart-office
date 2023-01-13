package plugin.atb.booking.service;

import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    private final TeamMemberService teamMemberService;

    @Transactional
    public void add(Team team) {

        boolean exists = teamRepository.existsByNameAndLeader(
            team.getName(), team.getLeader());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Данная команда уже существует: команда:%s, лидер:%s",
                team.getName(), team.getLeader().getFullName()));
        }

        if (team.getLeader() == null) {
            throw new NotFoundException("Лидер не найден.");
        }
        if (team.getName().isBlank()) {
            throw new NotFoundException("Название не найдено.");
        }

        teamRepository.save(team);

        var teamMember = new TeamMember().setTeam(team).setEmployee(team.getLeader());
        teamMemberService.add(teamMember);

    }

    public Page<Team> getAll(Pageable pageable) {

        return teamRepository.findAll(pageable);
    }

    public Page<Team> getAllByName(String name, Pageable pageable) {

        return teamRepository.findAllByNameContainingIgnoreCase(name, pageable);
    }

    public Team getById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public Page<Team> getAllByLeaderId(Long leaderId, Pageable pageable) {
        return teamRepository.findAllByLeaderId(leaderId, pageable);
    }

    @Transactional
    public void update(Team team) {

        var teamUpdate = getById(team.getId());

        if (teamUpdate == null) {
            throw new NotFoundException("Команда не найдена.");
        }

        var newName = team.getName();
        var newLeader = team.getLeader();

        boolean exists = teamRepository.existsByNameAndLeader(newName, newLeader);
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Лидер с id: %s уже закреплен за командой: %s",
                team.getLeader().getId(), team.getName()));
        }

        var oldMember = teamMemberService.getByEmployeeAndTeam(teamUpdate.getLeader(), teamUpdate);
        teamMemberService.delete(oldMember);
        var isMember = teamMemberService.getByEmployeeAndTeam(newLeader, teamUpdate);

        teamRepository.save(team);
        if (isMember == null) {
            var newTeamMember = new TeamMember().setTeam(team).setEmployee(newLeader);
            teamMemberService.add(newTeamMember);
        }
    }

    @Transactional
    public void delete(Long id) {

        var team = getById(id);
        if (team == null) {
            throw new NotFoundException(String.format(
                "Команда не найдена: %s", id));
        }

        teamMemberService.deleteAllByTeam(team);

        teamRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll(Set<Team> teams) {

        teams.forEach(teamMemberService::deleteAllByTeam);

        teamRepository.deleteAll(teams);
    }

}
