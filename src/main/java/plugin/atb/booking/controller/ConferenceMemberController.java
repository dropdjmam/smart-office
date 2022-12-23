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
@RequestMapping("/conferee")
public class ConferenceMemberController {

    private final ConferenceMemberService conferenceMemberService;

    private final ConferenceMemberMapper conferenceMemberMapper;

    private final BookingService bookingService;

    private final EmployeeService employeeService;

    @PostMapping("/")
    public ResponseEntity<String> createConferenceMember(
        @RequestBody ConferenceMemberCreateDto dto
    ) {

        var employee = employeeService.getById(dto.getEmployeeId());
        if (employee == null) {
            throw new NotFoundException(String.format(
                "Не найдена сотрудник с id: %s", dto.getEmployeeId()));
        }
        var booking = bookingService.getById(dto.getBookingId());
        if (booking == null) {
            throw new NotFoundException(String.format(
                "Не найдена сотрудник с id: %s", dto.getBookingId()));
        }
        var conferenceMember = conferenceMemberMapper
            .dtoToCreateConferenceMember(employee, booking);

        conferenceMemberService.add(conferenceMember);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Получить всех участников переговоров")
    @GetMapping("/all")
    public ResponseEntity<Page<ConferenceMemberCreateDto>> getConferenceMember(
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var conferee = conferenceMemberService.getAll(
            pageable);

        var dto = conferee.stream()
            .map(conferenceMemberMapper::createConferenceMemberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, conferee.getPageable(), conferee.getTotalElements())
        );

    }

    @Operation(summary = "Получить всех участников переговоров по id брони")
    @GetMapping("/all/booking/{bookingId}")
    public ResponseEntity<Page<ConferenceMemberDto>> getAllByBookingId(
        @PathVariable Long bookingId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        ValidationUtils.checkId(bookingId);
        var page = conferenceMemberService.getAllByBookingId(
            bookingId, pageable);

        var dto = page
            .stream()
            .map(conferenceMemberMapper::conferenceMemberToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Получить участника переговоров по id брони")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ConferenceMemberDto> getByBookingId(
        @PathVariable Long bookingId
    ) {
        ValidationUtils.checkId(bookingId);
        var conferenceMember = conferenceMemberService.getByBookingId(bookingId);

        if (conferenceMember == null) {
            throw new NotFoundException(String.format(
                "Не найдена команда с id: %s", bookingId));
        }

        return ResponseEntity.ok(conferenceMemberMapper.conferenceMemberToDto(conferenceMember));
    }

    @Operation(summary = "Получить участника переговоров по id сотрудника")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ConferenceMemberDto> getByEmployeeId(
        @PathVariable Long employeeId
    ) {
        ValidationUtils.checkId(employeeId);
        var conferenceMember = conferenceMemberService.getByEmployeeId(employeeId);

        if (conferenceMember == null) {
            throw new NotFoundException(String.format(
                "Не найдена команда с id: %s", employeeId));
        }

        return ResponseEntity.ok(conferenceMemberMapper.conferenceMemberToDto(conferenceMember));
    }

    @Operation(summary = "Получить участника переговоров по id")
    @GetMapping("/{id}")
    public ResponseEntity<ConferenceMemberCreateDto> getById(@PathVariable Long id) {
        ValidationUtils.checkId(id);
        var conferee = conferenceMemberService.getById(id);

        if (conferee == null) {
            throw new NotFoundException("Не найден участник переговоров с id: " + id);
        }

        return ResponseEntity.ok(conferenceMemberMapper.createConferenceMemberToDto(conferee));
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody ConferenceMemberDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());
        if (employee == null) {
            throw new NotFoundException(String.format(
                "Не найдена сотрудник с id: %s", dto.getEmployeeId()));
        }
        var booking = bookingService.getById(dto.getBookingId());
        if (booking == null) {
            throw new NotFoundException(String.format(
                "Не найдена сотрудник с id: %s", dto.getBookingId()));
        }
        var conferenceMember = conferenceMemberMapper.dtoToConferenceMember(
            dto, employee, booking);

        conferenceMemberService.update(conferenceMember);

        return ResponseEntity.ok("Данные участника переговоров успешно измененны");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        ValidationUtils.checkId(id);
        conferenceMemberService.delete(id);

        return ResponseEntity.ok("Участник переговоров успешно удален");
    }

}
