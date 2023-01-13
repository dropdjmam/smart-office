package plugin.atb.booking.controller;

import javax.validation.*;

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
import plugin.atb.booking.model.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@Slf4j
@RestController
@Tag(name = "Этаж")
@RequiredArgsConstructor
@RequestMapping("/floor")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class FloorController {

    private final OfficeService officeService;

    private final FloorService floorService;

    private final FloorMapper floorMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить этаж", description = "Все поля обязательны")
    public Long createFloor(@Valid @RequestBody FloorCreateDto dto) {

        var office = validateOffice(dto.getOfficeId());

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return floorService.add(floorMapper.dtoToFloor(dto, office));
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все этажи офиса")
    public Page<FloorGetDto> getFloors(
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

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить указанный этаж")
    public FloorGetDto getFloorById(@PathVariable Long id) {

        var floor = floorService.getById(id);
        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return floorMapper.floorToDto(floor);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменить указанный этаж", description = "Все поля обязательны")
    public String updateFloor(@Valid @RequestBody FloorUpdateDto dto) {

        var office = officeService.getById(dto.getOfficeId());

        floorService.update(floorMapper.dtoToFloor(dto, office));

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Этаж успешно изменен";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить этаж")
    public String deleteFloor(@PathVariable Long id) {

        floorService.delete(id);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Этаж успешно удален";
    }

    private Office validateOffice(long id) {
        var office = officeService.getById(id);
        if (office == null) {
            throw new NotFoundException("Не найден офис с id: " + id);
        }
        return office;
    }

}

