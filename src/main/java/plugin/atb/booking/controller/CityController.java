package plugin.atb.booking.controller;

import java.util.*;

import javax.validation.constraints.*;

import lombok.*;
import org.springframework.http.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/city")
public class CityController {

    private final CityService cityService;

    private final CityMapper cityMapper;

    @PostMapping("/")
    public ResponseEntity<String> createCity(@NotBlank @RequestParam String name) {
        cityService.add(name);

        return ResponseEntity.ok("Город " + name + "успешно создан.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<CityDto>> getCities() {
        List<CityDto> cities;

        cities = cityService.getAll()
            .stream()
            .map(cityMapper::cityToDto)
            .toList();

        return ResponseEntity.ok(cities);
    }

    @GetMapping("/id{id}")
    public ResponseEntity<CityDto> getCityById(@PathVariable Long id) {
        if (id < 1) {
            throw new IllegalArgumentException("Id не может быть меньше 1!");
        }

        return ResponseEntity.ok(cityMapper.cityToDto(cityService.getById(id))
        );
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<CityDto>> getCitiesByName(@PathVariable String name) {
        List<CityDto> cities;

        cities = cityService.getAllByName(name).stream()
            .map(cityMapper::cityToDto)
            .toList();

        return ResponseEntity.ok(cities);
    }

    @Validated
    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody CityDto dto) {
        cityService.update(cityMapper.dtoToCity(dto));

        return ResponseEntity.ok("Успешно! Город изменен: " + dto.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCity(@PathVariable Long id) {
        if (id < 1) {
            throw new IllegalArgumentException("Id не может быть меньше 1!");
        }

        cityService.delete(id);

        return ResponseEntity.ok("Город успешно удален.");
    }

}
