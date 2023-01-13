package plugin.atb.booking.controller;

import javax.validation.*;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@Slf4j
@RestController
@Tag(name = "Тип места")
@RequiredArgsConstructor
@RequestMapping("/workPlaceType")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class WorkPlaceTypeController {

    private final WorkPlaceTypeService workPlaceTypeService;

    private final WorkPlaceTypeMapper workPlaceTypeMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление нового типа места")
    public String createType(@RequestParam String typeName) {

        if (typeName.isBlank()) {
            throw new IncorrectArgumentException(
                "Имя типа места не может быть пустым или состоять только из пробелов");
        }

        workPlaceTypeService.add(typeName);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Успешно добавлен новый тип места: " + typeName;
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить все типы мест")
    public Page<WorkPlaceTypeDto> getAll(@ParameterObject Pageable pageable) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var type = workPlaceTypeService.getAll(pageable);

        var dto = type.stream()
            .map(workPlaceTypeMapper::typeToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, type.getPageable(), type.getTotalElements());
    }

    @GetMapping("/name")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить тип места по названию")
    public Page<WorkPlaceTypeDto> getByName(
        @Valid
        @NotBlank(message = "Название типа не может быть пустым или состоять только из пробелов")
        @RequestParam String name,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var page = workPlaceTypeService.getByName(
            name, pageable);

        var dto = page
            .stream()
            .map(workPlaceTypeMapper::typeToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить тип места по id")
    public WorkPlaceTypeDto getById(@PathVariable Long id) {
        ValidationUtils.checkId(id);
        var type = workPlaceTypeService.getById(id);

        if (type == null) {
            throw new NotFoundException(String.format("Не найден тип места c id: %s", id));
        }

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return workPlaceTypeMapper.typeToDto(type);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменить тип места")
    public String update(@Valid @RequestBody WorkPlaceTypeDto dto) {

        var type = workPlaceTypeMapper.dtoToType(dto);

        workPlaceTypeService.update(type);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return String.format("Тип места успешно изменён: %s", type.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить тип места")
    public String delete(@PathVariable Long id) {

        workPlaceTypeService.delete(id);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Тип места успешно удален";
    }

}
