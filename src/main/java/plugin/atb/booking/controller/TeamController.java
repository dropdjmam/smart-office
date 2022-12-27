package plugin.atb.booking.controller;

import javax.validation.*;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
@Tag(name = "Команда", description = "Наименование команды и ее лидер")
public class TeamController {

    private final TeamService teamService;

    private final EmployeeService employeeService;

    private final TeamMapper teamMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание команды (Лидер команды сразу добавляется к ее участникам)")
    public String createTeam(@Valid @RequestBody TeamCreateDto dto) {
        var leader = employeeService.getById(dto.getLeaderId());
        if (leader == null) {
            throw new NotFoundException(String.format(
                "Не найден лидер c id: %s",
                dto.getLeaderId()));
        }
        var team = teamMapper.dtoToCreateTeam(dto, leader);
        teamService.add(team);

        return "Команда успешно создана";
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все команды")
    public Page<TeamGetDto> getTeam(@ParameterObject Pageable pageable) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var team = teamService.getAll(
            pageable);

        var dto = team.stream()
            .map(teamMapper::teamToGetDto)
            .toList();

        return new PageImpl<>(dto, team.getPageable(), team.getTotalElements());

    }

    @GetMapping("/allByName")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск среди команд по названию")
    public Page<TeamDto> getAllByName(
        @Valid
        @NotBlank(message = "Название команды не может быть пустым или состоять только из пробелов")
        @RequestParam String name,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var page = teamService.getAllByName(
            name, pageable);

        var dto = page
            .stream()
            .map(teamMapper::teamToDto)
            .toList();

        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all/leader/{leaderId}")
    @Operation(summary = "Получить все команды, где сотрудник является лидером по его id")
    public Page<TeamDto> getAllByLeaderId(
        @PathVariable Long leaderId, @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(leaderId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = teamService.getAllByLeaderId(
            leaderId, pageable);

        var dto = page
            .stream()
            .map(teamMapper::teamToDto)
            .toList();

        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить команду по id")
    public TeamDto getById(@PathVariable Long id) {
        ValidationUtils.checkId(id);
        var team = teamService.getById(id);

        if (team == null) {
            throw new NotFoundException(String.format("Не найдена команда c id: %s", id));
        }

        return teamMapper.teamToDto(team);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновление команды: смена лидера/названия (Все поля обязательны)",
    description = "Если лидером назначается человек не из команды - то он добавляется к ее участникам")
    public String update(@Valid @RequestBody TeamDto dto) {

        var leader = employeeService.getById(dto.getLeaderId());
        if (leader == null) {
            throw new NotFoundException(String.format(
                "Не найден лидер c id: %s",
                dto.getLeaderId()));
        }
        var team = teamMapper.dtoToTeam(dto, leader);

        teamService.update(team);

        return "Изменение команды прошло успешно";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление команды (Все участники команды также удаляются)")
    public String delete(@PathVariable Long id) {

        teamService.delete(id);

        return "Команда успешно удалена";
    }

}
