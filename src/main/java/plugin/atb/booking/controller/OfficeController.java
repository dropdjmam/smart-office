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
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@Tag(name = "Офис")
@RequiredArgsConstructor
@RequestMapping("/office")
public class OfficeController {

    private final OfficeService officeService;

    private final CityService cityService;

    private final OfficeMapper officeMapper;

    @PostMapping("/")
    @Operation(summary = "Создание офиса", description = "Все поля обязательны")
    public ResponseEntity<Long> createOffice(@Valid @RequestBody OfficeCreateDto dto) {

        var city = cityService.getById(dto.getCityId());

        if (city == null) {
            throw new NotFoundException("Не найден город с id: " + dto.getCityId());
        }

        var newId = officeService.add(officeMapper.dtoToOffice(dto, city));

        return ResponseEntity.ok(newId);
    }

    @GetMapping("/all")
    @Operation(summary = "Все офисы", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<OfficeGetDto>> getOffices(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var offices = officeService.getAll(pageable);

        var dto = offices.stream()
            .map(officeMapper::officeToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, offices.getPageable(), offices.getTotalElements())
        );

    }

    @GetMapping("/allByAddress")
    @Operation(summary = "Поиск среди офисов по адресу ", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<OfficeGetDto>> getOfficesByAddress(
        @RequestParam String address,
        @ParameterObject Pageable pageable
    ) {

        if (address.isBlank()) {
            throw new IncorrectArgumentException(
                "Адрес не может быть пустым или состоять только из пробелов");
        }

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = officeService.getAllByAddress(address, pageable);

        var dto = page
            .stream()
            .map(officeMapper::officeToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение указанного офиса")
    public ResponseEntity<OfficeGetDto> getOfficeById(@PathVariable Long id) {

        ValidationUtils.checkId(id);

        var office = officeService.getById(id);
        if (office == null) {
            throw new NotFoundException("Не найден офис с id: " + id);
        }

        return ResponseEntity.ok(officeMapper.officeToDto(office));
    }

    @PutMapping("/")
    @Operation(summary = "Изменение указанного офиса", description = "Все поля обязательны")
    public ResponseEntity<String> update(@Valid @RequestBody OfficeUpdateDto dto) {

        var city = cityService.getById(dto.getCityId());
        if (city == null) {
            throw new NotFoundException("Не найден город с id: " + dto.getCityId());
        }

        officeService.update(officeMapper.dtoToOffice(dto, city));

        return ResponseEntity.ok("Офис успешно обновлен");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление указанного офиса")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        officeService.delete(id);

        return ResponseEntity.ok("Офис успешно удален");
    }

}
