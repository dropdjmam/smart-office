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
@Tag(name = "Тип места")
@RequiredArgsConstructor
@RequestMapping("/workPlaceType")
public class WorkPlaceTypeController {

    private final WorkPlaceTypeService workPlaceTypeService;

    private final WorkPlaceTypeMapper workPlaceTypeMapper;

    @PostMapping("/")
    public ResponseEntity<String> createType(
        @Valid
        @RequestBody WorkPlaceTypeCreateDto dto
    ) {

        var type = workPlaceTypeMapper.createDtoToType(dto);

        workPlaceTypeService.add(type.getName());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Получить все типы мест")
    @GetMapping("/all")
    public ResponseEntity<Page<WorkPlaceTypeDto>> getAll(
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var type = workPlaceTypeService.getAll(
            pageable);

        var dto = type.stream()
            .map(workPlaceTypeMapper::typeToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, type.getPageable(), type.getTotalElements())
        );

    }

    @Operation(summary = "Получить тип места по названию")
    @GetMapping("/type/name")
    public ResponseEntity<Page<WorkPlaceTypeDto>> getByName(
        @Valid
        @NotBlank(message = "Название типа не может быть пустым или состоять только из пробелов")
        @RequestBody String name,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var page = workPlaceTypeService.getByName(
            name, pageable);

        var dto = page
            .stream()
            .map(workPlaceTypeMapper::typeToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkPlaceTypeDto> getById(
        @PathVariable Long id
    ) {
        ValidationUtils.checkId(id);
        var type = workPlaceTypeService.getById(id);

        if (type == null) {
            throw new NotFoundException(String.format("Не найден тип места c id: %s", id));
        }

        return ResponseEntity.ok(workPlaceTypeMapper.typeToDto(type));
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody WorkPlaceTypeDto dto) {

        var type = workPlaceTypeMapper.dtoToType(dto);

        workPlaceTypeService.update(type);

        return ResponseEntity.ok(String.format(
            "Тип места успешно изменён: %s", type.getName()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        workPlaceTypeService.delete(id);

        return ResponseEntity.ok("Тип места успешно удален");
    }

}
