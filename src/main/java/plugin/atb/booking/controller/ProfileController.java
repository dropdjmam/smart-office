package plugin.atb.booking.controller;

import java.time.*;
import java.util.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "Профиль сотрудника", description = "Информация о сотруднике, ближайшая бронь и команда")
public class ProfileController {

    private final EmployeeMapper employeeMapper;

    private final EmployeeService employeeService;

    private final BookingService bookingService;

    private final TeamMemberService teamMemberService;

    private final BookingInfoMapper bookingInfoMapper;

    private final TeamMemberMapper teamMemberMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение своего профиля сотрудника",
        description = "Включает в себя данные о сотруднике, ближайшую бронь и команду")
    public ProfileDto getProfile() {

        var self = employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        return getProfileDto(self);
    }

    @GetMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение профиля указанного сотрудника",
        description = "Включает в себя данные о сотруднике, ближайшую бронь и команду")
    public ProfileDto getAnotherProfile(@PathVariable Long employeeId) {

        ValidationUtils.checkId(employeeId);

        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + employeeId);
        }

        return getProfileDto(employee);
    }

    private ProfileDto getProfileDto(Employee employee) {

        TeamMemberInfoTeamDto firstTeam = null;

        var teamMemberPage = teamMemberService.getAllByEmployee(employee, Pageable.ofSize(1));
        if (!teamMemberPage.isEmpty()) {
            var member = teamMemberPage.getContent().get(0);

            var membersPage = teamMemberService.getAllByTeam(member.getTeam(), Pageable.unpaged());
            var membersNumber = membersPage.getTotalElements();

            firstTeam = teamMemberMapper.teamMemberToInfoTeamDto(member, membersNumber);
        }

        var bookingPage = bookingService.getAllActual(employee, Pageable.ofSize(1));
        if (bookingPage.isEmpty()) {
            log.info("Operation successful, method {}", TraceUtils.getMethodName(2));
            return new ProfileDto(employeeMapper.employeeToDto(employee), null, firstTeam);
        }

        var booking = bookingPage.getContent().get(0);

        var placeInfo = Optional.of(booking)
            .map(Booking::getWorkPlace)
            .map(bookingInfoMapper::placeToDto)
            .orElse(null);

        if (placeInfo == null) {
            log.debug("Not found WorkPlace while forming BookingGetDto from {}", booking);
            return new ProfileDto(employeeMapper.employeeToDto(employee), null, firstTeam);
        }

        var officeInfo = Optional.of(booking)
            .map(Booking::getWorkPlace)
            .map(WorkPlace::getFloor)
            .map(Floor::getOffice)
            .map(bookingInfoMapper::officeToDto)
            .orElse(null);

        if (officeInfo == null) {
            log.debug("Not found Floor in WorkPlace or Office in Floor while " +
                      "forming BookingGetDto from {}", booking);
            return new ProfileDto(employeeMapper.employeeToDto(employee), null, firstTeam);
        }

        var bookingInfo = bookingInfoMapper.bookingToDto(
            booking,
            ZoneId.of(officeInfo.getZoneId()));

        var firstBooking = new BookingGetDto(bookingInfo, placeInfo, officeInfo);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(2));
        return new ProfileDto(employeeMapper.employeeToDto(employee), firstBooking, firstTeam);
    }

}
