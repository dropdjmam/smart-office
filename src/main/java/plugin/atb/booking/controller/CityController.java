package plugin.atb.booking.controller;

import java.util.*;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/city")
@Tag(name = "Город", description = "Наименование и тайм зона города")
public class CityController {

    private final CityService cityService;

    private final CityMapper cityMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление города")
    public String createCity(@RequestParam String name, @RequestParam String zoneId) {
        cityService.add(name, zoneId);

        return "Город " + name + "успешно добавлен";
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить список городов")
    public List<CityDto> getCities() {
        var cities = cityService.getAll();

        return cities.stream()
            .map(cityMapper::cityToDto)
            .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить город")
    public CityDto getCityById(@PathVariable Long id) {
        var city = cityService.getById(id);

        if (city == null) {
            throw new NotFoundException("Не найден город с id: " + id);
        }

        return cityMapper.cityToDto(city);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить список всех городов")
    public List<CityDto> getCitiesByName(@RequestParam String name) {
        var cities = cityService.getAllByName(name);

        return cities.stream()
            .map(cityMapper::cityToDto)
            .toList();
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменение наименования города")
    public String update(@Valid @RequestBody CityDto dto) {
        cityService.update(cityMapper.dtoToCity(dto));

        return "Успешно! Город изменен: " + dto.getName();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление города")
    public ResponseEntity<String> deleteCity(@PathVariable Long id) {

        cityService.delete(id);

        return ResponseEntity.ok("Город успешно удален.");
    }

}
