package plugin.atb.booking.controller;

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
import plugin.atb.booking.entity.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "Профиль сотрудника")
public class ProfileController {

    private final EmployeeMapper employeeMapper;

    private final EmployeeService employeeService;

    private final BookingService bookingService;

    private final TeamMemberService teamMemberService;

    private final TeamMapper teamMapper;

    private final BookingInfoMapper bookingInfoMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всей информации своего профиля сотрудника",
        description = "Включает в себя данные о сотруднике, ближайшую бронь и команду")
    public ProfileDto getProfile() {

        var self = employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        var teamMemberPage = teamMemberService.getAllTeamMemberByEmployee(self, Pageable.ofSize(1));
        var firstTeam = Optional.of(teamMemberPage.getContent().get(0))
            .map(TeamMemberEntity::getTeam)
            .map(teamMapper::teamToDto)
            .orElse(null);

        var booking = bookingService.getAllActual(self, Pageable.ofSize(1)).getContent().get(0);

        if (booking == null) {
            return new ProfileDto(employeeMapper.employeeToDto(self), null, firstTeam);
        }

        var bookingInfo = bookingInfoMapper.bookingToDto(booking);

        var placeInfo = Optional.of(booking)
            .map(BookingEntity::getWorkPlace)
            .map(bookingInfoMapper::placeToDto)
            .orElse(null);

        if (placeInfo == null) {
            log.debug("Not found WorkPlace while forming BookingGetDto from {}", booking);
            return new ProfileDto(employeeMapper.employeeToDto(self), null, firstTeam);
        }

        var officeInfo = Optional.of(booking)
            .map(BookingEntity::getWorkPlace)
            .map(WorkPlaceEntity::getFloor)
            .map(FloorEntity::getOffice)
            .map(bookingInfoMapper::officeToDto)
            .orElse(null);

        if (officeInfo == null) {
            log.debug("Not found Floor in WorkPlace or Office in Floor while " +
                      "forming BookingGetDto from {}", booking);
            return new ProfileDto(employeeMapper.employeeToDto(self), null, firstTeam);
        }

        var firstBooking = new BookingGetDto(bookingInfo, placeInfo, officeInfo);
        log.info("Profile successfully formed");
        return new ProfileDto(employeeMapper.employeeToDto(self), firstBooking, firstTeam);
    }

}
