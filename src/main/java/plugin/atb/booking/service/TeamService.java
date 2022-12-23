package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    private final TeamMemberService teamMemberService;

    @Transactional
    public void add(TeamEntity team) {

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

        var teamMember = new TeamMemberEntity().setTeam(team).setEmployee(team.getLeader());
        teamMemberService.add(teamMember);

    }

    public Page<TeamEntity> getAll(Pageable pageable) {

        return teamRepository.findAll(pageable);
    }

    public Page<TeamEntity> getAllByName(String name, Pageable pageable) {

        return teamRepository.findAllByName(name, pageable);
    }

    public TeamEntity getById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public Page<TeamEntity> getAllByLeaderId(Long leaderId, Pageable pageable) {
        return teamRepository.findAllByLeaderId(leaderId, pageable);
    }

    public void update(TeamEntity team) {

        if (getById(team.getId()) == null) {
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

        teamRepository.save(team);
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

}
