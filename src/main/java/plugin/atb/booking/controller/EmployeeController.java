package plugin.atb.booking.controller;

import java.util.*;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import lombok.extern.slf4j.*;
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

@Slf4j
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
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание сотрудника",
        description = "Все поля кроме роли обязательны (роль по дефолту ROLE_EMPLOYEE - id 1)")
    public String createEmployee(@Valid @RequestBody EmployeeCreateDto dto) {

        var role = Optional.of(dto)
            .map(EmployeeCreateDto::getRoleId)
            .map(roleService::getById)
            .orElse(roleService.getById(1L));

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        employeeService.add(employeeMapper.dtoToEmployee(dto, role));

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Сотрудник успешно добавлен";
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Все сотрудники", description = "1 <= size <= 20 (default 20)")
    public Page<EmployeeGetDto> getEmployees(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = employeeService.getPage(pageable);

        var dto = page.stream()
            .map(employeeMapper::employeeToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/allByName")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск среди сотрудников по имени", description = "1 <= size <= 20 (default 20)")
    public Page<EmployeeGetDto> getEmployeesByName(
        @RequestParam String name, @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = employeeService.getPageByName(name, pageable);

        var dto = page.stream()
            .map(employeeMapper::employeeToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить сотрудника по id")
    public EmployeeGetDto getEmployeeById(@PathVariable Long id) {

        var employee = employeeService.getById(id);

        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + id);
        }

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return employeeMapper.employeeToDto(employee);
    }

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить сотрудника по логину")
    public EmployeeGetDto getEmployeeByLogin(@RequestParam String login) {

        var employee = employeeService.getByLogin(login);

        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с логином: " + login);
        }

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return employeeMapper.employeeToDto(employee);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменение указанного сотрудника",
        description = "Все поля обязательны")
    public String update(@Valid @RequestBody EmployeeUpdateDto dto) {

        var role = roleService.getById(dto.getRoleId());
        if (role == null) {
            throw new NotFoundException("Не найдена роль с id: " + dto.getRoleId());
        }

        employeeService.update(employeeMapper.dtoToEmployee(dto, role));

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Данные сотрудника успешно обновлены";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление указанного сотрудника по id",
        description = "Сотрудник также удаляется из участников команд, а также, если сотрудник " +
                      "является лидером команды - она удаляется")
    public String delete(@PathVariable Long id) {

        employeeService.delete(id);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Сотрудник успешно удален";
    }

}
