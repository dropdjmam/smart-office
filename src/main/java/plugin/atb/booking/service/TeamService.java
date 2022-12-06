package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public void add(TeamEntity team) {

        boolean exists = teamRepository.existsByNameAndLeader(
            team.getName(), team.getLeader());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Данная команда уже существует: %s, %s",
                team.getName(), team.getLeader()));
        }

        if (team.getLeader() == null) {
            throw new NotFoundException(String.format(
                "Лидер не найден: %s", team.getLeader()));
        }

        teamRepository.save(team);
    }

    public Page<TeamEntity> getAll(Pageable pageable) {

        return teamRepository.findAll(pageable);
    }

    public Page<TeamEntity> getAllByName(String name, Pageable pageable) {

        return teamRepository.findAllByName(name, pageable);
    }

    public Page<TeamEntity> getAllById(Long id, Pageable pageable) {

        return teamRepository.findAllById(id, pageable);
    }

    public TeamEntity getById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public TeamEntity getByLeaderId(Long id) {
        return teamRepository.findByLeaderId(id);
    }

    public void update(TeamEntity team) {
        TeamEntity updateTeam = getById(team.getId());

        if (updateTeam == null) {
            throw new NotFoundException(String.format(
                "Команда не найдена: %s", team.getId()));
        }

        if (team.getName() != null) {
            updateTeam.setName(team.getName());
        }

        if (team.getLeader() != null) {
            updateTeam.setLeader(team.getLeader());
        }

        teamRepository.save(team);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format(
                "Команда не найдена: %s", id));
        }

        teamRepository.deleteById(id);
    }

}
