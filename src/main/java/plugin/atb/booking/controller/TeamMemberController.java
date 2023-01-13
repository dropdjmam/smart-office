package plugin.atb.booking.controller;

import java.util.stream.*;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team_member")
@Tag(name = "Участник команды")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    private final TeamMemberMapper teamMemberMapper;

    private final EmployeeService employeeService;

    private final TeamService teamService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание участника команды/добавление сотрудника к команде")
    public String createTeamMember(@Valid @RequestBody TeamMemberCreateDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());
        if (employee == null) {
            throw new NotFoundException(String.format(
                "Сотрудник с id:%s не найден", dto.getEmployeeId()));
        }
        var team = teamService.getById(dto.getTeamId());
        if (team == null) {
            throw new NotFoundException(String.format(
                "Команда с id:%s не найдена", dto.getTeamId()));
        }
        var teamMember = teamMemberMapper.dtoToCreateTeamMember(team, employee);

        teamMemberService.add(teamMember);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Сотрудник успешно добавлен в команду";
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить всех участников команд")
    public Page<TeamMemberDto> getAll(@ParameterObject Pageable pageable) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = teamMemberService.getAll(
            pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::teamMemberToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all/team/{teamId}")
    @Operation(summary = "Получить всех участников команды по id команды")
    public Page<TeamMemberInfoDto> getAllTeamMemberByTeamId(
        @PathVariable Long teamId, @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(teamId);

        var team = teamService.getById(teamId);

        if (team == null) {
            throw new NotFoundException("Не найдена команда с id: " + teamId);
        }

        var page = teamMemberService.getAllByTeam(team, pageable);

        var infoDto = page
            .stream()
            .map(teamMemberMapper::teamMemberToInfoDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(infoDto, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all/employee/{employeeId}")
    @Operation(summary = "Получить все команды, в которых состоит сотрудник, по id сотрудника")
    public Page<TeamMemberInfoTeamDto> getAllTeamByEmployeeId(
        @PathVariable Long employeeId,
        @PageableDefault(sort = "teamId") @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(employeeId);

        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник по id: " + employeeId);
        }

        var page = teamMemberService.getAllByEmployee(employee, pageable);
        var teamMemberShip = page.getContent();

        var membersNumberByTeam = teamMemberShip.stream()
            .map(TeamMember::getTeam)
            .map(t -> teamMemberService.getAllByTeam(t, Pageable.unpaged()))
            .map(Page::getTotalElements)
            .toList();

        var dto = IntStream.range(0, membersNumberByTeam.size())
            .mapToObj(i -> teamMemberMapper.teamMemberToInfoTeamDto(
                teamMemberShip.get(i),
                membersNumberByTeam.get(i)))
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/employee/{employeeId}/team/{teamId}")
    @Operation(summary = "Удаление сотрудника из команды по его id и id команды")
    public String delete(@PathVariable Long employeeId, @PathVariable Long teamId) {

        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException(String.format("Не найден сотрудник с id: %s", employeeId));
        }
        var team = teamService.getById(teamId);
        if (team == null) {
            throw new NotFoundException(String.format("Не найдена команда с id: %s", teamId));
        }
        var teamMember = teamMemberService.getByEmployeeAndTeam(employee, team);
        if (teamMember == null) {
            throw new NotFoundException(String.format(
                "Не найден участник команды по сотруднику с id: %s, команде с id: %s",
                employeeId, teamId));
        }
        teamMemberService.delete(teamMember);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Участник команды успешно удален";
    }

}
