package plugin.atb.booking.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/administrating")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Администрирование", description = "Администрирование - доступ админа к функционалу офиса")
public class AdministratingController {

    private final AdministratingService administratingService;

    private final AdministratingMapper administratingMapper;

    private final EmployeeService employeeService;

    private final OfficeService officeService;

    private final OfficeMapper officeMapper;

    private final EmployeeMapper employeeMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление администрирования/предоставление доступа к офису")
    public String createAdmin(@RequestBody AdministratingCreateDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + dto.getEmployeeId());
        }

        var office = officeService.getById(dto.getOfficeId());
        if (office == null) {
            throw new NotFoundException("Не найден офис с id: " + dto.getOfficeId());
        }

        var administrating = administratingMapper.dtoToAdministrating(employee, office);

        administratingService.add(administrating);

        return String.format(
            "Доступ к офису с id: %s успешно предоставлен администратору с id: %s",
            office.getId(), employee.getId());
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все \"Администрирования\"")
    public Page<AdministratingDto> getAdmin(@ParameterObject Pageable pageable) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var page = administratingService.getAll(pageable);

        var dtos = page.stream()
            .map(administratingMapper::administratingToDto)
            .toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/officesOfAdmin/{employeeId}")
    @Operation(summary = "Получить все офисы по id администратора")
    public Page<OfficeGetDto> getAllOfficeById(
        @PathVariable Long employeeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(employeeId);

        var page = administratingService.getAllByEmployeeId(employeeId, pageable);

        var dtos = page
            .map(Administrating::getOffice)
            .map(officeMapper::officeToDto)
            .toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/adminsOfOffice/{officeId}")
    @Operation(summary = "Получить всех администраторов по id офиса")
    public Page<EmployeeGetDto> getAllByOfficeId(
        @PathVariable Long officeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(officeId);

        var page = administratingService.getAllByOfficeId(officeId, pageable);

        var dtos = page
            .map(Administrating::getEmployee)
            .map(employeeMapper::employeeToDto)
            .toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление администрирования/убрать доступ к функционалу офиса у админа")
    public String delete(@Parameter(description = "Id администрирования") @PathVariable Long id) {

        administratingService.delete(id);

        return "Доступ к функционалу офиса успешно убран";
    }

}
