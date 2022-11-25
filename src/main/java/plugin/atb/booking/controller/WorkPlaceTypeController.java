package plugin.atb.booking.controller;

import java.util.*;

import javax.validation.constraints.*;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workPlaceType")
public class WorkPlaceTypeController {
    private final WorkPlaceTypeService workPlaceTypeService;

    private final WorkPlaceTypeMapper workPlaceTypeMapper;

    @PostMapping("/")
    public ResponseEntity<String> createWorkPlaceType(@NotBlank @RequestParam String name) {
        workPlaceTypeService.add(name);

        return ResponseEntity.ok("Тип места успешно создан:" + name);
    }

    @GetMapping("/all")
    public ResponseEntity<List<WorkPlaceTypeDto>> getTypes() {
        List<WorkPlaceTypeDto> types;

        types = workPlaceTypeService.getAll()
            .stream()
            .map(workPlaceTypeMapper::typeToDto)
            .toList();

        return ResponseEntity.ok(types);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkPlaceTypeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workPlaceTypeMapper.typeToDto(workPlaceTypeService.getById(id))
        );
    }

    @GetMapping("/")
    public ResponseEntity<List<WorkPlaceTypeDto>> getAllByName(@RequestParam String name) {
        List<WorkPlaceTypeDto> types;

        types = workPlaceTypeService.getAllByName(name).stream()
            .map(workPlaceTypeMapper::typeToDto)
            .toList();

        return ResponseEntity.ok(types);
    }

    @PutMapping("/")
    public ResponseEntity<String> updateWorkPlaceType(WorkPlaceTypeDto dtoNewType) {

        workPlaceTypeService.updateWorkPlaceType(workPlaceTypeMapper.dtoToType(dtoNewType));

        return ResponseEntity.ok("Тип места изменен: " + dtoNewType.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkPlaceType(@PathVariable Long id) {
        workPlaceTypeService.deleteWorkPlaceType(id);

        return ResponseEntity.ok("Тип места удалён.");
    }

}
