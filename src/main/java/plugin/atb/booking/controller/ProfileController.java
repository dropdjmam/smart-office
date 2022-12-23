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

    private final TeamMemberService teamMemberService;

    private final TeamMapper teamMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всей информации своего профиля сотрудника",
        description = "Включает в себя данные о сотруднике, ближайшую бронь и команду")
    public ProfileDto getProfile() {

        var self = employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        var firstTeam = teamMemberService.getAllTeamMemberByEmployee(self, Pageable.ofSize(1))
            .map(TeamMemberEntity::getTeam)
            .map(teamMapper::teamToDto)
            .getContent().get(0);

        var firstOwnBooking = bookingService.getAllActual(self, Pageable.ofSize(1))
            .map(bookingMapper::bookingToDto)
            .getContent().get(0);

        return new ProfileDto(employeeMapper.employeeToDto(self), firstOwnBooking, firstTeam);
    }

}
