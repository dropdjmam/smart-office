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
@Tag(name = "Роль")
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    @PostMapping("/")
    @Operation(summary = "Добавление роли")
    public ResponseEntity<String> createRole(@RequestParam String name) {
        if (name.isBlank()) {
            throw new IncorrectArgumentException(
                "Имя роли не может быть пустым или состоять только из пробелов");
        }

        roleService.add(name);

        return ResponseEntity.ok("Роль успешно создана");
    }

    @GetMapping("/all")
    @Operation(summary = "Метод возвращает все роли")
    public ResponseEntity<List<RoleDto>> getRoles() {

        var roles = roleService.getAll()
            .stream()
            .map(roleMapper::roleToDto)
            .toList();

        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Метод возвращает роль по ее id")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {

        var role = roleService.getById(id);
        if (role == null) {
            throw new NotFoundException("Не найдена роль с id: " + id);
        }

        return ResponseEntity.ok(roleMapper.roleToDto(role));
    }

    @PutMapping("/")
    @Operation(summary = "Изменение роли")
    public ResponseEntity<String> update(@Valid @RequestBody RoleDto dtoWithNewName) {

        roleService.update(roleMapper.dtoToRole(dtoWithNewName));

        return ResponseEntity.ok("Успешно! Новое имя роли: " + dtoWithNewName.getName());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление роли")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        roleService.delete(id);

        return ResponseEntity.ok("Роль успешно удалена");
    }

}
