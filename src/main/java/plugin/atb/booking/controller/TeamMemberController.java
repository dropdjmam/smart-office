package plugin.atb.booking.controller;

import javax.validation.*;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.*;
import lombok.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team_member")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    private final TeamMemberMapper teamMemberMapper;

    private final EmployeeService employeeService;

    private final TeamService teamService;

    @PostMapping("/")
    public ResponseEntity<String> createTeamMember(@Valid @RequestBody TeamMemberCreateDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());

        var team = teamService.getById(dto.getTeamId());

        var teamMember = teamMemberMapper.dtoToCreateTeamMember(team, employee);

        teamMemberService.add(teamMember);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Получить всех участников команд")
    @GetMapping("/all")
    public ResponseEntity<Page<TeamMemberDto>> getAll(
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = teamMemberService.getAll(
            pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::teamMemberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Получить всех участников команды по id команды")
    @GetMapping("/all/teamId")
    public ResponseEntity<Page<TeamMemberDto>> getAllTeamMemberByTeamId(
        @RequestParam Long teamId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(teamId);
        var page = teamMemberService.getAllTeamMemberByTeamId(
            teamId, pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::teamMemberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Получить всех участников команды по названию команды")
    @GetMapping("/all/teamName")
    public ResponseEntity<Page<TeamMemberDto>> getAllTeamMemberByTeamName(
        @Valid
        @NotBlank(message = "Название команды не может быть пустым или состоять только из пробелов")
        @RequestParam String name,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = teamMemberService.getAllTeamMemberByTeamName(
            name, pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::teamMemberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Получить все команды по id сотрудника")
    @GetMapping("/all/employeeId")
    public ResponseEntity<Page<TeamMemberDto>> getAllTeamByEmployeeId(
        @RequestParam Long employeeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(employeeId);

        var page = teamMemberService.getAllTeamByEmployeeId(
            employeeId, pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::teamMemberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Получить участника команды по id команды")
    @GetMapping("/team")
    public ResponseEntity<TeamMemberDto> getByTeamId(
        @RequestBody Long teamId
    ) {
        ValidationUtils.checkId(teamId);
        var teamMember = teamMemberService.getByTeamId(teamId);

        if (teamMember == null) {
            throw new NotFoundException(String.format(
                "Не найдена команда с id: %s", teamId));
        }

        return ResponseEntity.ok(teamMemberMapper.teamMemberToDto(teamMember));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberDto> getById(
        @PathVariable Long id
    ) {
        ValidationUtils.checkId(id);
        var member = teamMemberService.getById(id);

        if (member == null) {
            throw new NotFoundException(String.format("Не найден участник команды с id: %s", id));
        }

        return ResponseEntity.ok(teamMemberMapper.teamMemberToDto(member));
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@Valid @RequestBody TeamMemberDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());

        var team = teamService.getById(dto.getTeamId());

        var teamMember = teamMemberMapper.dtoToTeamMember(dto, team, employee);

        teamMemberService.add(teamMember);

        return ResponseEntity.ok(String.format(
            "Данные участника команды успешно измененны: %s, %s", employee, team));
    }

    @DeleteMapping("/{employeeId}/{teamId}")
    public ResponseEntity<String> delete(
        @PathVariable Long employeeId,
        @PathVariable Long teamId
    ) {

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
                "Не найдена участник команды по сотруднику с id: %s, команде с id: %s",
                employeeId, teamId));
        }
        teamMemberService.delete(teamMember);

        return ResponseEntity.ok("Участник команды успешно удален");
    }

}
