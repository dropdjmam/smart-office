package plugin.atb.booking.controller;

import java.time.*;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Бронирования")
@RequestMapping("/booking")
public class BookingController {

    private final EmployeeService employeeService;

    private final WorkPlaceService workPlaceService;

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    @PostMapping("/")
    @Operation(summary = "Создание брони/бронирование места на указанного сотрудника",
        description = "Все поля кроме holder_id обязательны. Если holder_id не указан, то " +
                      "держателем брони назначается ее создатель. ")
    public ResponseEntity<String> createBooking(@Valid @RequestBody BookingCreateDto dto) {

        var maker = getSessionUser();
        var holder = validateHolder(dto.getHolderId());
        var place = validatePlace(dto.getWorkPlaceId());

        validateByOfficeWorkTime(place, dto.getStart().toLocalTime(), dto.getEnd().toLocalTime());

        var booking = bookingMapper.dtoToBooking(dto, holder, maker, place);
        bookingService.add(booking);

        return ResponseEntity.ok("Место успешно забронировано");
    }

    @Operation(summary = "Поиск всех актуальных броней указанного сотрудника",
        description = "1 <= size <= 20 (default 20)")
    @GetMapping("/allActual")
    public ResponseEntity<Page<BookingGetDto>> getActualBookings(
        @RequestParam Long holderId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(holderId);

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var holder = validateHolder(holderId);

        var page = bookingService.getAllActual(holder, pageable);

        var dto = page.stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @Operation(summary = "Поиск всех своих актуальных броней",
        description = "1 <= size <= 20 (default 20)")
    @GetMapping("/allActualSelf")
    public ResponseEntity<Page<BookingGetDto>> getSelfActualBookings(
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var holder = getSessionUser();

        var page = bookingService.getAllActual(holder, pageable);

        var dto = page.stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/allInPeriod")
    @Operation(summary = "Метод возвращает все брони по месту в указанном периоде времени",
        description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<BookingGetDto>> getBookingsInPeriod(
        @RequestParam Long placeId,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @RequestParam LocalDateTime start,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @RequestParam LocalDateTime end,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(placeId);

        var place = validatePlace(placeId);

        var page = bookingService.getAllInPeriod(place, start, end, pageable);

        var dto = page.stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/all")
    @Operation(summary = "Метод возвращает все брони (включая удаленные)",
        description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<BookingGetDto>> getBookings(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = bookingService.getAll(pageable);

        var dto = page.stream()
            .map(bookingMapper::bookingToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение указанной брони")
    public ResponseEntity<BookingGetDto> getBookingById(@PathVariable Long id) {

        var booking = bookingService.getById(id);
        if (booking == null) {
            throw new NotFoundException("Не найдена бронь с id: " + id);
        }

        var dto = bookingMapper.bookingToDto(booking);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/")
    @Operation(summary = "Изменение указанной брони")
    public ResponseEntity<String> update(@Valid @RequestBody BookingUpdateDto dto) {

        var maker = getSessionUser();
        var holder = validateHolder(dto.getHolderId());
        var place = validatePlace(dto.getWorkPlaceId());

        validateByOfficeWorkTime(place, dto.getStart().toLocalTime(), dto.getEnd().toLocalTime());

        var booking = bookingMapper.dtoToBooking(dto, holder, maker, place);
        bookingService.update(booking);

        return ResponseEntity.ok("Бронь успешно обновлена");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление указанной брони")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        bookingService.delete(id);

        return ResponseEntity.ok("Бронь успешно удалена");
    }

    private EmployeeEntity getSessionUser() {
        return employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName()
        );
    }

    private EmployeeEntity validateHolder(Long id) {
        var holder = employeeService.getById(id);
        if (holder == null) {
            throw new NotFoundException("Не найден держатель брони с id: " + id);
        }
        return holder;
    }

    private WorkPlaceEntity validatePlace(Long id) {
        var place = workPlaceService.getById(id);
        if (place == null) {
            throw new NotFoundException("Не найдено место с id: " + id);
        }
        return place;
    }

    private void validateByOfficeWorkTime(WorkPlaceEntity place, LocalTime start, LocalTime end) {
        var floor = place.getFloor();
        if (floor == null) {
            throw new NotFoundException("Не найден этаж у места с id: " + place.getId());
        }
        var office = floor.getOffice();
        if (office == null) {
            throw new NotFoundException(String.format(
                "У этажа с id %s по месту с id %s не найден офис",
                floor.getId(), place.getId()));
        }

        if (office.getStartOfDay() == null) {
            throw new NotFoundException(String.format(
                "У офиса не найдено начало рабочего дня. Id офиса: %s, Id этажа: %s, Id места: %s",
                office.getId(), floor.getId(), place.getId()));
        }

        if (office.getEndOfDay() == null) {
            throw new NotFoundException(String.format(
                "У офиса не найден конец рабочего дня. Id офиса: %s, Id этажа: %s, Id места: %s",
                office.getId(), floor.getId(), place.getId()));
        }

        if (start.isBefore(office.getStartOfDay())) {
            throw new IncorrectArgumentException(
                "Начало брони не может быть раньше начала рабочего дня офиса");
        }

        if (end.isAfter(office.getEndOfDay())) {
            throw new IncorrectArgumentException(
                "Конец брони не может быть позже конца рабочего дня офиса");
        }

    }

}
