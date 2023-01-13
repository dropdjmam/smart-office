package plugin.atb.booking.controller;

import java.util.*;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import lombok.extern.slf4j.*;
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
@RequiredArgsConstructor
@RequestMapping("/city")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Город", description = "Наименование и тайм зона города")
public class CityController {

    private final CityService cityService;

    private final CityMapper cityMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление города")
    public String createCity(@RequestParam String name, @RequestParam String zoneId) {
        cityService.add(name, zoneId);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Город " + name + "успешно добавлен";
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить список всех городов")
    public List<CityDto> getCities() {
        var cities = cityService.getAll();

        return cities.stream()
            .map(cityMapper::cityToDto)
            .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить город")
    public CityDto getCityById(@PathVariable Long id) {
        var city = cityService.getById(id);

        if (city == null) {
            throw new NotFoundException("Не найден город с id: " + id);
        }

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return cityMapper.cityToDto(city);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Поиск городов по наименованию")
    public List<CityDto> getCitiesByName(@RequestParam String name) {
        var cities = cityService.getAllByName(name);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return cities.stream()
            .map(cityMapper::cityToDto)
            .toList();
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменение наименования города")
    public String update(@Valid @RequestBody CityDto dto) {
        cityService.update(cityMapper.dtoToCity(dto));

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Успешно! Город изменен: " + dto.getName();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление города")
    public ResponseEntity<String> deleteCity(@PathVariable Long id) {

        cityService.delete(id);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return ResponseEntity.ok("Город успешно удален.");
    }

}
