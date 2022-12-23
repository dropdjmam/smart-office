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

    public void add(TeamMemberEntity member) {

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

    public TeamMemberEntity getByTeamId(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    public TeamMemberEntity getByTeamName(String name) {
        return teamMemberRepository.findByTeamName(name);
    }

    public void update(TeamMemberEntity member) {

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
