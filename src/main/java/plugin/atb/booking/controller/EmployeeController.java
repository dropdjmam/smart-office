package plugin.atb.booking.controller;

import java.util.*;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@Tag(name = "Сотрудник")
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final EmployeeService employeeService;

    private final EmployeeMapper employeeMapper;

    @PostMapping("/")
    @Operation(
        summary = "Создание сотрудника",
        description = "Все поля кроме роли и фото обязательны (роль по дефолту ROLE_EMPLOYEE - id 1)"
    )
    public ResponseEntity<String> createEmployee(@Valid @RequestBody EmployeeCreateDto dto) {

        var role = Optional.of(dto)
            .map(EmployeeCreateDto::getRoleId)
            .map(roleService::getById)
            .orElse(roleService.getById(1L));

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        employeeService.add(employeeMapper.dtoToEmployee(dto, role));

        return ResponseEntity.ok("Сотрудник успешно добавлен");
    }

    @GetMapping("/all")
    @Operation(summary = "Все сотрудники", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<EmployeeGetDto>> getEmployees(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = employeeService.getPage(pageable);

        var dto = page.stream()
            .map(employeeMapper::employeeToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/allByName")
    @Operation(summary = "Поиск среди сотрудников по имени", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<EmployeeGetDto>> getEmployeesByName(
        @RequestParam String name,
        @ParameterObject Pageable pageable
    ) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = employeeService.getPageByName(name, pageable);

        var dto = page.stream()
            .map(employeeMapper::employeeToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение указанного сотрудника по id")
    public ResponseEntity<EmployeeGetDto> getEmployeeById(@PathVariable Long id) {

        ValidationUtils.checkId(id);

        var employee = employeeService.getById(id);

        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + id);
        }

        return ResponseEntity.ok(employeeMapper.employeeToDto(employee));
    }

    @GetMapping("/login")
    public ResponseEntity<EmployeeGetDto> getEmployeeByLogin(@RequestParam String login) {

        var employee = employeeService.getByLogin(login);

        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с логином: " + login);
        }

        return ResponseEntity.ok(employeeMapper.employeeToDto(employee));
    }

    @PutMapping("/")
    @Operation(
        summary = "Изменение указанного сотрудника",
        description = "Все поля кроме фото обязательны")
    public ResponseEntity<String> update(@Valid @RequestBody EmployeeUpdateDto dto) {

        var role = roleService.getById(dto.getRoleId());
        if (role == null) {
            throw new NotFoundException("Не найдена роль с id: " + dto.getRoleId());
        }

        employeeService.update(employeeMapper.dtoToEmployee(dto, role));

        return ResponseEntity.ok("Данные сотрудника успешно обновлены");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление указанного сотрудника по id")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        employeeService.delete(id);

        return ResponseEntity.ok("Сотрудник успешно удален");
    }

}
