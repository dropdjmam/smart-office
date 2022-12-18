package plugin.atb.booking.controller;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
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

@Tag(name = "Рабочее место")
@RestController
@RequiredArgsConstructor
@RequestMapping("/workplace")
public class WorkPlaceController {

    private final WorkPlaceTypeService workPlaceTypeService;

    private final FloorService floorService;

    private final WorkPlaceService workPlaceService;

    private final WorkPlaceMapper workPlaceMapper;

    @PostMapping("/")
    @Operation(summary = "Создание рабочего места", description = "Все поля обязательны")
    public ResponseEntity<String> createWorkPlace(@Valid @RequestBody WorkPlaceCreateDto dto) {

        var type = typeValidate(dto.getTypeId());

        var floor = floorValidate(dto.getFloorId());

        workPlaceService.add(workPlaceMapper.dtoToWorkPlace(type, floor, dto.getCapacity()));

        return ResponseEntity.ok("Место успешно добавлено");
    }

    @GetMapping("/all")
    @Operation(summary = "Все рабочие места", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<WorkPlaceGetDto>> getPage(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = workPlaceService.getPage(pageable);

        var dto = page.stream()
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/allByFloor")
    @Operation(summary = "Все рабочие места на указанном этаже", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<WorkPlaceGetDto>> getPageByFloor(
        @RequestParam Long floorId,
        @ParameterObject Pageable pageable
    ) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var floor = floorValidate(floorId);

        var page = workPlaceService.getPageByFloor(floor, pageable);

        var dto = page.stream()
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение указанного места")
    public ResponseEntity<WorkPlaceGetDto> getWorkPlaceById(@PathVariable Long id) {

        var workPlace = workPlaceService.getById(id);
        if (workPlace == null) {
            throw new NotFoundException("Не найдено место с id: " + id);
        }

        return ResponseEntity.ok(workPlaceMapper.workPlaceToDto(workPlace));
    }

    @PutMapping("/")
    @Operation(summary = "Изменение указанного места", description = "Все поля обязательны")
    public ResponseEntity<String> update(@Valid @RequestBody WorkPlaceUpdateDto dto) {

        var type = typeValidate(dto.getTypeId());

        var floor = floorValidate(dto.getFloorId());

        workPlaceService.update(workPlaceMapper.dtoToWorkPlace(dto, type, floor));

        return ResponseEntity.ok("Место успешно обновлено");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление указанного места")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        workPlaceService.delete(id);

        return ResponseEntity.ok("Место успешно удалено");
    }

    private FloorEntity floorValidate(long id) {
        var floor = floorService.getById(id);
        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }
        return floor;
    }

    private WorkPlaceTypeEntity typeValidate(long id) {
        var type = workPlaceTypeService.getById(id);
        if (type == null) {
            throw new NotFoundException("Не найден тип места с id: " + id);
        }
        return type;
    }

}
