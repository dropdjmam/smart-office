package plugin.atb.booking.controller;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team_member")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    private final TeamMemberMapper teamMemberMapper;

    private final EmployeeService employeeService;

    private final TeamService teamService;

    @PostMapping("/")
    public ResponseEntity<String> createTeamMember(@RequestBody TeamMemberDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());

        var booking = bookingService.getById(dto.getBookingId());

        conferenceMemberService
            .add(conferenceMemberMapper.createDtoToConferee(dto, employee, booking));

        return ResponseEntity.ok(String.format(
            "Участник переговоров успешно зарегестрирован на бронь: %s, %s",
            employee, booking));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ConferenceMemberCreateDto>> getConferenceMember(
        @ParameterObject Pageable pageable
    ) {

        var conferee = conferenceMemberService.getAll(
            pageable);

        var dto = conferee.stream()
            .map(conferenceMemberMapper::confereeToCreateDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, conferee.getPageable(), conferee.getTotalElements())
        );

    }

    @GetMapping("/all/booking")
    public ResponseEntity<Page<ConferenceMemberCreateDto>> getAllByBooking(
        @RequestBody BookingEntity booking,
        @ParameterObject Pageable pageable
    ) {
        var page = conferenceMemberService.getAllByBooking(
            booking, pageable);

        var dto = page
            .stream()
            .map(conferenceMemberMapper::confereeToCreateDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<ConferenceMemberDto> getByEmployeeId(@PathVariable Long id) {

        var conferee = conferenceMemberService.getByEmployeeId(id);

        if (conferee == null) {
            throw new NotFoundException(String.format("Сотрудник не найден: %s", id));
        }

        return ResponseEntity.ok(conferenceMemberMapper.confereeToDto(conferee));
    }

    @GetMapping("/booking/{id}")
    public ResponseEntity<ConferenceMemberDto> getByBookingId(@PathVariable Long id) {

        var conferee = conferenceMemberService.getByBookingId(id);

        if (conferee == null) {
            throw new NotFoundException(String.format("Бронь не найдена: %s", id));
        }

        return ResponseEntity.ok(conferenceMemberMapper.confereeToDto(conferee));
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Page<ConferenceMemberDto>> getAllById(
        @PathVariable Long id,
        @ParameterObject Pageable pageable
    ) {
        var page = conferenceMemberService.getAllById(
            id, pageable);

        var dto = page
            .stream()
            .map(conferenceMemberMapper::confereeToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConferenceMemberCreateDto> getById(@PathVariable Long id) {

        var conferee = conferenceMemberService.getById(id);

        if (conferee == null) {
            throw new NotFoundException("Не найден участник переговоров с id: " + id);
        }

        return ResponseEntity.ok(conferenceMemberMapper.confereeToCreateDto(conferee));
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody ConferenceMemberUpdateDto dto) {

        var employee = employeeService.getById(dto.getEmployeeId());

        var booking = bookingService.getById(dto.getBookingId());

        conferenceMemberService
            .update(conferenceMemberMapper.dtoToUpdateConferee(dto, employee, booking));

        return ResponseEntity.ok(String.format(
            "Данные участника переговоров успешно измененны: %s, %s", employee, booking));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        conferenceMemberService.delete(id);

        return ResponseEntity.ok("Участник переговоров успешно удален");
    }

}
