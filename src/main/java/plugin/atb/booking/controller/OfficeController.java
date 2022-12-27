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
@RequiredArgsConstructor
@RequestMapping("/office")
@Tag(name = "Офис", description = "Интервал работы офиса принимается и хранится в LocalTime " +
                                  "в соответствии с тайм зоной города, в котором находится.")
public class OfficeController {

    private final OfficeService officeService;

    private final CityService cityService;

    private final OfficeMapper officeMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать офис", description = "Все поля обязательны")
    public Long createOffice(@Valid @RequestBody OfficeCreateDto dto) {

        var city = cityService.getById(dto.getCityId());

        if (city == null) {
            throw new NotFoundException("Не найден город с id: " + dto.getCityId());
        }

        return officeService.add(officeMapper.dtoToOffice(dto, city));
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Все офисы", description = "1 <= size <= 20 (default 20)")
    public Page<OfficeGetDto> getOffices(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var offices = officeService.getAll(pageable);

        var dto = offices.stream()
            .map(officeMapper::officeToDto)
            .toList();

        return new PageImpl<>(dto, offices.getPageable(), offices.getTotalElements());
    }

    @GetMapping("/allByAddress")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск среди офисов по адресу", description = "1 <= size <= 20 (default 20)")
    public Page<OfficeGetDto> getOfficesByAddress(
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

        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить указанный офис")
    public OfficeGetDto getOfficeById(@PathVariable Long id) {

        var office = officeService.getById(id);
        if (office == null) {
            throw new NotFoundException("Не найден офис с id: " + id);
        }

        return officeMapper.officeToDto(office);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменить указанный офис", description = "Все поля обязательны")
    public String update(@Valid @RequestBody OfficeUpdateDto dto) {

        var city = cityService.getById(dto.getCityId());
        if (city == null) {
            throw new NotFoundException("Не найден город с id: " + dto.getCityId());
        }

        officeService.update(officeMapper.dtoToOffice(dto, city));

        return "Офис успешно изменен";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить указанный офис")
    public String delete(@PathVariable Long id) {

        officeService.delete(id);

        return "Офис успешно удален";
    }

}
