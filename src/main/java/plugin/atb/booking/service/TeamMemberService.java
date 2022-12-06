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
                team.getEmployee(), team.getTeam()));
        }

        teamMemberRepository.save(team);
    }

    public Page<TeamMemberEntity> getAll(Pageable pageable) {

        return teamMemberRepository.findAll(pageable);
    }

    public Page<TeamMemberEntity> getAllByEmployee(EmployeeEntity employee, Pageable pageable) {

        return teamMemberRepository.findAllByEmployee(employee, pageable);
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

        if (team.getEmployee() != null) {
            updateTeamMember.setEmployee(team.getEmployee());
        }

        if (team.getTeam() != null) {
            updateTeamMember.setTeam(team.getTeam());
        }

        teamMemberRepository.save(team);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format(
                "Участник команды не найден: %s", id));
        }

        teamMemberRepository.deleteById(id);
    }

}
