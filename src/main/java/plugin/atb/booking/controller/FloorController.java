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

@RestController
@Tag(name = "Этаж")
@RequiredArgsConstructor
@RequestMapping("/floor")
public class FloorController {

    private final OfficeService officeService;

    private final FloorService floorService;

    private final FloorMapper floorMapper;

    @PostMapping("/")
    @Operation(summary = "Добавление этажа", description = "Все поля кроме карты обязательны")
    public ResponseEntity<String> createFloor(@Valid @RequestBody FloorCreateDto dto) {

        var office = validateOffice(dto.getOfficeId());

        floorService.add(floorMapper.dtoToFloor(dto, office));

        return ResponseEntity.ok("Этаж успешно создан");
    }

    @GetMapping("/all")
    @Operation(summary = "Поиск всех этажей офиса")
    public ResponseEntity<Page<FloorDto>> getFloors(
        @RequestParam Long officeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        ValidationUtils.checkId(officeId);

        var office = validateOffice(officeId);

        var page = floorService.getAllByOffice(office, pageable);

        var dto = page.stream()
            .map(floorMapper::floorToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение указанного этажа")
    public ResponseEntity<FloorDto> getFloorById(@PathVariable Long id) {

        var floor = floorService.getById(id);
        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }

        return ResponseEntity.ok(floorMapper.floorToDto(floor));
    }

    @PutMapping("/")
    @Operation(summary = "Изменение указанного этажа", description = "Все поля кроме карты обязательны")
    public ResponseEntity<String> updateFloor(@Valid @RequestBody FloorDto dto) {

        var office = officeService.getById(dto.getOfficeId());

        floorService.update(floorMapper.dtoToFloor(dto, office));

        return ResponseEntity.ok("Этаж успешно изменен");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление этажа")
    public ResponseEntity<String> deleteFloor(@PathVariable Long id) {

        floorService.delete(id);

        return ResponseEntity.ok("Этаж успешно удален");
    }

    private OfficeEntity validateOffice(long id) {
        var office = officeService.getById(id);
        if (office == null) {
            throw new NotFoundException("Не найден офис с id: " + id);
        }
        return office;
    }

}

