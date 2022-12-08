package plugin.atb.booking.controller;

import javax.validation.*;
import javax.validation.constraints.*;

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

        teamMemberService
            .add(teamMemberMapper.dtoToCreateMember(team, employee));

        return ResponseEntity.ok(String.format("%s успешно добавлен в команду: %s",
            employee.getFullName(), team.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<TeamMemberDto>> getAll(
        @ParameterObject Pageable pageable
    ) {
        var page = teamMemberService.getAll(
            pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::memberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/all/teamId")
    public ResponseEntity<Page<TeamMemberDto>> getAllTeamMemberByTeamId(
        @Valid
        @NotNull(message = "Вы ничего не ввели")
        @Min(value = 1L, message = "id не может быть меньше единицы")
        @RequestParam Long teamId,
        @ParameterObject Pageable pageable
    ) {
        var page = teamMemberService.getAllTeamMemberByTeamId(
            teamId, pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::memberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/all/teamName")
    public ResponseEntity<Page<TeamMemberDto>> getAllTeamMemberByTeamName(
        @Valid
        @NotBlank(message = "Название команды не может быть пустым или состоять только из пробелов")
        @RequestParam String name,
        @ParameterObject Pageable pageable
    ) {
        var page = teamMemberService.getAllTeamMemberByTeamName(
            name, pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::memberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/all/employeeId")
    public ResponseEntity<Page<TeamMemberDto>> getAllTeamByEmployeeId(
        @Valid
        @NotNull(message = "Вы ничего не ввели")
        @Min(value = 1L, message = "id не может быть меньше единицы")
        @RequestParam Long employeeId,
        @ParameterObject Pageable pageable
    ) {
        var page = teamMemberService.getAllTeamByEmployeeId(
            employeeId, pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::memberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/team")
    public ResponseEntity<Page<TeamMemberDto>> getByTeam(
        @RequestBody TeamEntity team,
        @ParameterObject Pageable pageable
    ) {

        var page = teamMemberService.getByTeam(
            team, pageable);

        var dto = page
            .stream()
            .map(teamMemberMapper::memberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMemberDto> getById(
        @Min(value = 1L, message = "id не может быть меньше единицы")
        @PathVariable Long id
    ) {

        var member = teamMemberService.getById(id);

        if (member == null) {
            throw new NotFoundException(String.format("Не найден участник команды с id: %s", id));
        }

        return ResponseEntity.ok(teamMemberMapper.memberToDto(member));
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@Valid @RequestBody TeamMemberDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());

        var team = teamService.getById(dto.getTeamId());

        teamMemberService
            .update(teamMemberMapper.dtoToMember(dto, team, employee));

        return ResponseEntity.ok(String.format(
            "Данные участника команды успешно измененны: %s, %s", employee, team));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        teamMemberService.delete(id);

        return ResponseEntity.ok("Участник команды успешно удален");
    }

}
