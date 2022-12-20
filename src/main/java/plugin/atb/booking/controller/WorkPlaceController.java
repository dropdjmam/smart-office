package plugin.atb.booking.controller;

import java.util.stream.*;

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

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

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
        @RequestParam Long typeId,
        @ParameterObject Pageable pageable
    ) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var type = validateType(typeId);

        var floor = validateFloor(floorId);

        var page = workPlaceService.getPageByFloorAndType(floor, type, pageable);

        var dto = page.stream()
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/allIsFreeByFloor")
    @Operation(summary = "Все свободные места одного типа на указанном этаже",
        description = "Для запроса необходим id этажа, id типа места, " +
                      "начало временного интервала и конец, а также " +
                      "параметры пагинации. 1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<PlaceAvailabilityResponseDto>> getIsFreeByFloor(
        @Valid @ModelAttribute @ParameterObject PlaceAvailabilityRequestDto dto,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

        var floorPlaces = workPlaceService.getPageByFloorAndType(floor, type, pageable);

        if (floorPlaces.isEmpty()) {
            return ResponseEntity.ok(Page.empty(floorPlaces.getPageable()));
        }

        var freePlaces = workPlaceService.getAllFreeInPeriod(
            floorPlaces.getContent(),
            dto.getStart(),
            dto.getEnd());

        var freeIds = freePlaces.stream()
            .map(WorkPlaceEntity::getId)
            .collect(Collectors.toSet());

        var responseDtos = floorPlaces.stream()
            .map(p -> workPlaceMapper.placeToPlaceAvailabilityDto(p, freeIds.contains(p.getId())))
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            responseDtos,
            floorPlaces.getPageable(),
            floorPlaces.getTotalElements()));
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

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

        workPlaceService.update(workPlaceMapper.dtoToWorkPlace(dto, type, floor));

        return ResponseEntity.ok("Место успешно обновлено");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление указанного места")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        workPlaceService.delete(id);

        return ResponseEntity.ok("Место успешно удалено");
    }

    private FloorEntity validateFloor(long id) {
        var floor = floorService.getById(id);
        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }
        return floor;
    }

    private WorkPlaceTypeEntity validateType(long id) {
        var type = workPlaceTypeService.getById(id);
        if (type == null) {
            throw new NotFoundException("Не найден тип места с id: " + id);
        }
        return type;
    }

}
