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
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    private final EmployeeService employeeService;

    private final TeamMapper teamMapper;

    @PostMapping("/")
    public ResponseEntity<String> createTeam(
        @Valid
        @RequestBody TeamCreateDto dto
    ) {
        var leader = employeeService.getById(dto.getLeaderId());
        if (leader == null) {
            throw new NotFoundException(String.format(
                "Не найден лидер c id: %s",
                dto.getLeaderId()));
        }
        var team = teamMapper.dtoToCreateTeam(dto, leader);
        teamService.add(team);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Получить все команды")
    @GetMapping("/all")
    public ResponseEntity<Page<TeamGetDto>> getTeam(
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var team = teamService.getAll(
            pageable);

        var dto = team.stream()
            .map(teamMapper::teamToGetDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, team.getPageable(), team.getTotalElements())
        );

    }

    @Operation(summary = "Получить все команды по названию")
    @GetMapping("/allByName")
    public ResponseEntity<Page<TeamDto>> getAllByName(
        @Valid
        @NotBlank(message = "Название команды не может быть пустым или состоять только из пробелов")
        @RequestBody String name,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var page = teamService.getAllByName(
            name, pageable);

        var dto = page
            .stream()
            .map(teamMapper::teamToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Получить все команды по id тим-лидера")
    @GetMapping("/all/leader/{leaderId}")
    public ResponseEntity<Page<TeamDto>> getAllByLeaderId(
        @PathVariable Long leaderId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(leaderId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = teamService.getAllByLeaderId(
            leaderId, pageable);

        var dto = page
            .stream()
            .map(teamMapper::teamToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Получить команду по id")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getById(
        @PathVariable Long id
    ) {
        ValidationUtils.checkId(id);
        var team = teamService.getById(id);

        if (team == null) {
            throw new NotFoundException(String.format("Не найдена команда c id: %s", id));
        }

        return ResponseEntity.ok(teamMapper.teamToDto(team));
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody TeamDto dto) {

        var leader = employeeService.getById(dto.getLeaderId());
        if (leader == null) {
            throw new NotFoundException(String.format(
                "Не найден лидер c id: %s",
                dto.getLeaderId()));
        }
        var team = teamMapper.dtoToTeam(dto, leader);

        teamService.update(team);

        return ResponseEntity.ok("Изменение команды прошло успешно");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        teamService.delete(id);

        return ResponseEntity.ok("Команда успешно удален");
    }

}
