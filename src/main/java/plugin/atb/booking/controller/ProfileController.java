package plugin.atb.booking.controller;

import java.util.*;

import lombok.*;
import org.springframework.http.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ProfileController {

    private final EmployeeMapper employeeMapper;

    private final EmployeeService employeeService;

    private final BookingMapper bookingMapper;

    private final BookingService bookingService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile() {

        String login = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

        EmployeeEntity self = employeeService.getByLogin(login);

        List<BookingGetDto> bookings;

        bookings = bookingService.getAllActual(self.getId())
            .stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        ProfileDto profile = new ProfileDto(
            employeeMapper.employeeToDto(self),
            bookings);

        return ResponseEntity.ok(profile);
    }

}
