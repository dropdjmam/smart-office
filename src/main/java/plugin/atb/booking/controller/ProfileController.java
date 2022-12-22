package plugin.atb.booking.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "Профиль сотрудника")
public class ProfileController {

    private final EmployeeMapper employeeMapper;

    private final EmployeeService employeeService;

    private final BookingMapper bookingMapper;

    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Получение всей информации своего профиля сотрудника",
        description = "Включает в себя данные о сотруднике, его актуальные брони и команды," +
                      "в которых он состоит")
    public ResponseEntity<ProfileDto> getProfile() {

        String login = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

        EmployeeEntity self = employeeService.getByLogin(login);

        var bookings = bookingService.getAllActual(self, Pageable.ofSize(20))
            .stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        var profile = new ProfileDto(employeeMapper.employeeToDto(self), bookings);

        return ResponseEntity.ok(profile);
    }

}
