package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void add(TeamMember member) {

        boolean exists = teamMemberRepository.existsByEmployeeAndTeam(
            member.getEmployee(), member.getTeam());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Участник с id:%s уже состоит в команде с id:%s",
                member.getEmployee().getId(), member.getTeam().getId()));
        }
        if (member.getEmployee() == null) {
            throw new NotFoundException("Сотрудник не найден.");
        }

        if (member.getTeam() == null) {
            throw new NotFoundException("Команда не найдена.");
        }
        teamMemberRepository.save(member);
    }

    public Page<TeamMember> getAll(Pageable pageable) {

        return teamMemberRepository.findAll(pageable);
    }

    public TeamMember getByEmployeeAndTeam(
        Employee employee, Team team
    ) {

        return teamMemberRepository.findTeamMemberByEmployeeAndTeam(employee, team);
    }

    public Page<TeamMember> getAllTeamMemberByTeamId(Long teamId, Pageable pageable) {

        return teamMemberRepository.findAllTeamMemberByTeamId(teamId, pageable);
    }

    public Page<TeamMember> getAllTeamMemberByTeamName(String name, Pageable pageable) {

        return teamMemberRepository.findAllTeamMemberByTeamName(name, pageable);
    }

    public Page<TeamMember> getAllTeamMemberByEmployee(Employee employee, Pageable pageable) {
        return teamMemberRepository.findAllTeamByEmployee(employee, pageable);
    }

    public TeamMember getById(Long id) {
        return teamMemberRepository.findById(id).orElse(null);
    }

    public TeamMember getByTeamId(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    public TeamMember getByTeamName(String name) {
        return teamMemberRepository.findByTeamName(name);
    }

    @Transactional
    public void update(TeamMember member) {

        if (getById(member.getId()) == null) {
            throw new NotFoundException("Не найдена роль с id: " + member.getId());
        }

        boolean exists = teamMemberRepository.existsByEmployeeAndTeam(
            member.getEmployee(), member.getTeam());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Участник с id:%s уже состоит в команде с id:%s",
                member.getEmployee().getId(), member.getTeam().getId()));
        }

        teamMemberRepository.save(member);
    }

    @Transactional
    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format("Участник команды не найден: %s", id));
        }

        teamMemberRepository.deleteById(id);
    }

    @Transactional
    public void delete(TeamMember teamMember) {

        if (teamMember == null) {
            throw new IncorrectArgumentException("Участник команды не указан");
        }

        teamMemberRepository.delete(teamMember);
    }

    @Transactional
    public void deleteAllByTeam(Team team) {
        if (team == null) {
            throw new IncorrectArgumentException("Команда для удаления ее участников не указана");
        }

        teamMemberRepository.deleteAllByTeam(team);
    }

}
