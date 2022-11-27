package plugin.atb.booking.controller;

import java.time.*;
import java.util.*;

import lombok.*;
import org.springframework.http.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private final EmployeeService employeeService;

    private final WorkPlaceService workPlaceService;

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    @PostMapping("/")
    public ResponseEntity<String> createBooking(@RequestBody BookingCreateDto dto) {

        var maker = employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName()
        );

        var holder = Optional.of(dto)
            .map(BookingCreateDto::getHolderId)
            .map(employeeService::getById)
            .orElse(maker);

        if (holder == null) {
            throw new NotFoundException("Сотрудник не найден");
        }

        var workPlace = workPlaceService.getById(dto.getWorkPlaceId());

        var booking = bookingMapper.dtoToEntity(
            dto,
            holder,
            maker,
            workPlace
        );
        bookingService.add(booking);

        return ResponseEntity.ok("Место успешно забронировано");
    }

    @GetMapping("/allActual")
    public ResponseEntity<List<BookingGetDto>> getActualBookings(@RequestParam Long id) {
        EmployeeEntity holder;

        if (id > 0) {
            holder = employeeService.getById(id);

        } else {
            String login = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
            holder = employeeService.getByLogin(login);
        }

        if (holder == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + id);
        }

        var dto = bookingService.getAllActual(holder.getId()).stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/allInPeriod")
    public ResponseEntity<List<BookingGetDto>> getValidBookings(
        @RequestBody Long workplaceId,
        LocalDateTime start,
        LocalDateTime end
    ) {
        var workPlace = workPlaceService.getById(workplaceId);

        var dto = bookingService.getAllInPeriod(workPlace, start, end).stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingGetDto>> getBookingsPage(@RequestParam Integer pageNumber) {

        var dto = bookingService.getPage(pageNumber).stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/allSelf")
    public ResponseEntity<List<BookingGetDto>> getSelfBookingsPage(@RequestParam Integer pageNumber) {

        var holder = employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        var dto = bookingService.getPage(pageNumber).stream()
            .map(bookingMapper::bookingToDto)
            .filter(b -> Objects.equals(b.getHolderId(), holder.getId()))
            .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingGetDto> getBookingById(@PathVariable Long id) {

        var dto = bookingMapper.bookingToDto(bookingService.getById(id));

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/")
    public ResponseEntity<String> update(@RequestBody BookingUpdateDto dto) {

        var maker = employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName()
        );

        var workPlace = workPlaceService.getById(dto.getNewWorkPlaceId());

        bookingService.update(bookingMapper.dtoToEntity(dto, maker, workPlace));

        return ResponseEntity.ok("Бронь успешно обновлена");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        bookingService.delete(id);

        return ResponseEntity.ok("Бронь успешно удалена");
    }

}
