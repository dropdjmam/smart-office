package plugin.atb.booking.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/conferee")
@Tag(name = "Участник переговорки/конференции",
    description = "Участники одной переговорки/конференции (далее встречи) имеют одну общую бронь")
public class ConferenceMemberController {

    private final ConferenceMemberService conferenceMemberService;

    private final ConferenceMemberMapper conferenceMemberMapper;

    private final BookingService bookingService;

    private final EmployeeService employeeService;

    private final EmployeeMapper employeeMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление одного участника встречи")
    public String createConferenceMember(@RequestBody ConferenceMemberCreateDto dto) {
        var employee = employeeService.getById(dto.getEmployeeId());
        if (employee == null) {
            throw new NotFoundException(String.format(
                "Не найден сотрудник с id: %s", dto.getEmployeeId()));
        }
        var booking = bookingService.getById(dto.getBookingId());
        if (booking == null) {
            throw new NotFoundException(String.format(
                "Не найдена бронь с id: %s", dto.getBookingId()));
        }
        var conferenceMember = conferenceMemberMapper
            .dtoToCreateConferenceMember(employee, booking);

        conferenceMemberService.add(conferenceMember);

        return "Участник встречи успешно добавлен";
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить всех участников всех встреч")
    public Page<ConferenceMemberCreateDto> getConferenceMember(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = conferenceMemberService.getAll(pageable);

        var dto = page.stream()
            .map(conferenceMemberMapper::createConferenceMemberToDto)
            .toList();

        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/employeesOfBooking/{bookingId}")
    @Operation(summary = "Получить всех участников встреч (сотрудников) по id брони")
    public Page<EmployeeGetDto> getAllByBookingId(
        @PathVariable Long bookingId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(bookingId);

        var page = conferenceMemberService.getAllByBookingId(bookingId, pageable);

        if (page.isEmpty()) {
            return Page.empty();
        }

        var dtos = page.stream()
            .map(ConferenceMember::getEmployee)
            .map(employeeMapper::employeeToDto)
            .toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление участника встречи")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        conferenceMemberService.delete(id);

        return ResponseEntity.ok("Участник встречи успешно удален");
    }

}
