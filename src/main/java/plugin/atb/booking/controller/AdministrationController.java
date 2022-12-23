package plugin.atb.booking.controller;

import io.swagger.v3.oas.annotations.*;
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
@RequestMapping("/administration")
public class AdministrationController {

    private final AdministrationService administrationService;

    private final AdministrationMapper administrationMapper;

    private final EmployeeService employeeService;

    private final OfficeService officeService;

    @PostMapping("/")
    public ResponseEntity<String> createAdmin(@RequestBody AdministrationDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + dto.getEmployeeId());
        }
        var office = officeService.getById(dto.getOfficeId());
        if (office == null) {
            throw new NotFoundException("Не найден офис с id: " + dto.getOfficeId());
        }
        var admin = administrationMapper.createDtoToAdmin(employee, office);

        administrationService.add(admin);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Получить всех администраторов")
    @GetMapping("/all")
    public ResponseEntity<Page<AdministrationDto>> getAdmin(
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var admin = administrationService.getAll(
            pageable);

        var dto = admin.stream()
            .map(administrationMapper::adminToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, admin.getPageable(), admin.getTotalElements())
        );

    }

    @Operation(summary = "Получить все офисы по id администартора")
    @GetMapping("/allOffice/{id}")
    public ResponseEntity<Page<AdministrationDto>> getAllOfficeById(
        @PathVariable Long id,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(id);
        var page = administrationService.getAllOfficeById(
            id, pageable);

        var dto = page
            .stream()
            .map(administrationMapper::adminToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Получить всех администраторов по id офиса")
    @GetMapping("/allAdministration/office/{officeId}")
    public ResponseEntity<Page<AdministrationDto>> getAllAdministrationByOfficeId(
        @PathVariable Long officeId,
        @ParameterObject Pageable pageable
    ) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(officeId);
        var page = administrationService.getAllAdministrationByOfficeId(
            officeId, pageable);

        var dto = page
            .stream()
            .map(administrationMapper::adminToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrationDto> getById(@PathVariable Long id) {
        ValidationUtils.checkId(id);

        var admin = administrationService.getById(id);

        if (admin == null) {
            throw new NotFoundException("Не найден администратор с id: " + id);
        }

        return ResponseEntity.ok(administrationMapper.adminToDto(admin));
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody AdministrationDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + dto.getEmployeeId());
        }
        var office = officeService.getById(dto.getOfficeId());
        if (office == null) {
            throw new NotFoundException("Не найден офис с id: " + dto.getOfficeId());
        }
        var admin = administrationMapper.createDtoToAdmin(employee, office);

        administrationService.update(admin);

        return ResponseEntity.ok("Данные администратора измененны");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        administrationService.delete(id);

        return ResponseEntity.ok("Администратор успешно удален");
    }

}
