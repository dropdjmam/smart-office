package plugin.atb.booking.service;

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

    public TeamMember getByEmployeeAndTeam(Employee employee, Team team) {
        return teamMemberRepository.findByEmployeeAndTeam(employee, team);
    }

    public Page<TeamMember> getAllByTeam(Team team, Pageable pageable) {
        return teamMemberRepository.findAllByTeam(team, pageable);
    }

    public Page<TeamMember> getAllByEmployee(Employee employee, Pageable pageable) {
        return teamMemberRepository.findAllByEmployee(employee, pageable);
    }

    public TeamMember getById(Long id) {
        return teamMemberRepository.findById(id).orElse(null);
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
