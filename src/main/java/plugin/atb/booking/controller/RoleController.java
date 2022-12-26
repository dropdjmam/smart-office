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
@RequestMapping("/role")
@Tag(name = "Роль", description = "Изначально имеется 3 роли: сотрудник, админ и тимлид")
public class RoleController {

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить роль")
    public String createRole(@RequestParam String name) {
        if (name.isBlank()) {
            throw new IncorrectArgumentException(
                "Имя роли не может быть пустым или состоять только из пробелов");
        }

        roleService.add(name);

        return "Роль успешно создана";
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все роли")
    public List<RoleDto> getRoles() {

        return roleService.getAll().stream()
            .map(roleMapper::roleToDto)
            .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить роль по ее id")
    public RoleDto getRoleById(@PathVariable Long id) {

        var role = roleService.getById(id);
        if (role == null) {
            throw new NotFoundException("Не найдена роль с id: " + id);
        }

        return roleMapper.roleToDto(role);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменить имя роли")
    public String update(@Valid @RequestBody RoleDto dtoWithNewName) {

        roleService.update(roleMapper.dtoToRole(dtoWithNewName));

        return "Успешно! Новое имя роли: " + dtoWithNewName.getName();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить роль")
    public String delete(@PathVariable Long id) {

        roleService.delete(id);

        return "Роль успешно удалена";
    }

}
