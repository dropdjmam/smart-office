package plugin.atb.booking.controller;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import javax.validation.*;

import static java.time.ZoneOffset.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Бронирования")
@RequestMapping("/booking")
public class BookingController {

    private final EmployeeService employeeService;

    private final WorkPlaceService workPlaceService;

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;

    private final BookingInfoMapper infoMapper;

    private final TeamMemberService teamMemberService;

    private final ConferenceMemberService conferenceMemberService;

    private final OfficeService officeService;

    private final WorkPlaceTypeService workPlaceTypeService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать бронь места на указанного сотрудника",
        description = "Все поля обязательны")
    public String createBooking(@Valid @RequestBody BookingCreateDto dto) {

        var maker = getSessionUser();
        var holder = validateHolder(dto.getHolderId());
        var place = validatePlace(dto.getWorkPlaceId());

        var start = dto.getStart();
        validateBookingStart(start);

        var end = dto.getEnd();
        validateByOfficeWorkTime(place, start.toLocalTime(), end.toLocalTime());
        validateIsAlreadyBooked(place, start, end);

        var booking = bookingMapper.dtoToBooking(dto, holder, maker, place);
        bookingService.add(booking);
        log.info("Booking successfully created");

        return "Место успешно забронировано";
    }

    @PostMapping("/team")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать брони места для команды",
        description = "В контексте данного метода holderId -> id команды, держателем " +
                      "брони назначается лидер команды")
    public String createBookingForTeam(@Valid @RequestBody BookingCreateDto dto) {

        var teamMembers = teamMemberService.getAllTeamMemberByTeamId(
            dto.getHolderId(),
            Pageable.unpaged());

        var count = teamMembers.getSize() + dto.getGuests();

        var place = validatePlaceAndCapacity(dto.getWorkPlaceId(), count);

        var start = dto.getStart();
        validateBookingStart(start);

        var end = dto.getEnd();
        validateByOfficeWorkTime(place, start.toLocalTime(), end.toLocalTime());
        validateIsAlreadyBooked(place, start, end);

        var maker = getSessionUser();
        var leaderAsHolder = teamMembers.getContent().get(0).getTeam().getLeader();
        var employees = teamMembers.stream()
            .map(TeamMember::getEmployee)
            .collect(Collectors.toSet());

        var booking = bookingMapper.dtoToBooking(dto, leaderAsHolder, maker, place);
        var newBooking = bookingService.add(booking);

        var conferees = employees.stream()
            .map(e -> new ConferenceMember().setBooking(newBooking).setEmployee(e))
            .collect(Collectors.toSet());
        conferenceMemberService.addAll(conferees);
        log.info("Booking and conferees successfully created");

        return "Место успешно забронировано, участники брони созданы";
    }

    @PostMapping("/group")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать брони места для указанной группы людей",
        description = "Бронирующий назначается держателем брони. " +
                      "Если бронирующий хочет добавить себя в участники - добавить его id список.")
    public String createBookingForGroup(@Valid @RequestBody BookingGroupCreateDto dto) {

        var makerSameHolder = getSessionUser();

        var bookingMembers = dto.getHolderIds().stream()
            .map(this::validateHolder)
            .collect(Collectors.toSet());

        var count = bookingMembers.size() + dto.getGuests();
        var place = validatePlaceAndCapacity(dto.getWorkPlaceId(), count);

        var start = dto.getStart();
        validateBookingStart(start);

        var end = dto.getEnd();
        validateByOfficeWorkTime(place, start.toLocalTime(), end.toLocalTime());
        validateIsAlreadyBooked(place, start, end);

        var booking = bookingMapper.dtoToBooking(dto, makerSameHolder, makerSameHolder, place);
        var newBooking = bookingService.add(booking);

        var conferees = bookingMembers.stream()
            .map(m -> new ConferenceMember().setBooking(newBooking).setEmployee(m))
            .collect(Collectors.toSet());
        conferenceMemberService.addAll(conferees);
        log.info("Booking and conferees successfully created");

        return "Место успешно забронировано, участники брони созданы";
    }

    @GetMapping("/conference")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить брони с переговорок указанного сотрудника",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> getConferenceBookings(
        @RequestParam Long employeeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(employeeId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник по id: " + employeeId);
        }

        var page = conferenceMemberService.getAllActualByEmployee(employee, pageable);

        var bookings = page.map(ConferenceMember::getBooking);

        var dtos = bookings.stream()
            .map(this::getBookingDto)
            .toList();

        log.info("Page of conference place's bookings successfully formed");

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/allActual")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить актуальные брони указанного сотрудника - держателя броней",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> getActualBookings(
        @RequestParam Long holderId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(holderId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var holder = validateHolder(holderId);

        var page = bookingService.getAllActual(holder, pageable);

        var dtos = page.stream()
            .map(this::getBookingDto)
            .toList();

        log.info("Page of employee's actual bookings successfully formed");

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allActualSelf")
    @Operation(summary = "Получить актуальные брони пользователя сессии - держателя броней",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> getSelfActualBookings(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var holder = getSessionUser();

        var page = bookingService.getAllActual(holder, pageable);

        var dtos = page.stream()
            .map(this::getBookingDto)
            .toList();

        log.info("Page of session user's actual bookings successfully formed");

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/allInPeriod")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить брони по месту в указанном периоде времени",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> getBookingsInPeriod(
        @RequestParam Long placeId,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @RequestParam LocalDateTime start,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") @RequestParam LocalDateTime end,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(placeId);

        var place = validatePlace(placeId);

        var page = bookingService.getAllInPeriod(place, start, end, pageable);

        var placeInfo = infoMapper.placeToDto(place);

        var dtos = page.stream()
            .map(b -> new BookingGetDto(
                infoMapper.bookingToDto(b),
                placeInfo,
                getOfficeInfo(b)))
            .toList();

        log.info("Page of bookings by place and date/time interval successfully formed");

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все брони (включая удаленные)",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> getBookings(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = bookingService.getAll(pageable);

        var dtos = page.stream()
            .map(this::getBookingDto)
            .toList();

        log.info("Page of all (include deleted) bookings successfully formed");

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/allByHolder/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить брони указанного сотрудника - держателя броней",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> getHolderBookings(
        @PathVariable Long id,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var holder = validateHolder(id);

        var page = bookingService.getHolderHistory(holder, pageable);

        var dtos = page.stream()
            .map(this::getBookingDto)
            .toList();

        log.info("Page of employee's bookings successfully formed");

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/allSelf")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить брони пользователя сессии - держателя броней",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> getSelfBookings(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var holder = getSessionUser();

        var page = bookingService.getHolderHistory(holder, pageable);

        var dtos = page.stream()
            .map(this::getBookingDto)
            .toList();

        log.info("Page of session user's actual bookings successfully formed");

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/office/")
    @Operation(summary = "Получение страницы броней по офису (включая удаленные)")
    public Page<BookingGetDto> getAllOfOffice(
        @RequestParam Long officeId,
        @RequestParam Long typeId,
        @Parameter(description = "Параметр отвечает за выборку броней: актуальные - все. " +
                                 "Параметр не обязателен, дефолтное значение - true.")
        @RequestParam(required = false, defaultValue = "true") Boolean isActual,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(officeId);
        ValidationUtils.checkId(typeId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var office = officeService.getById(officeId);
        if (office == null) {
            throw new NotFoundException("Не найден офис с id: " + officeId);
        }

        var type = workPlaceTypeService.getById(typeId);
        if (type == null) {
            throw new NotFoundException("Не найден тип места с id: " + typeId);
        }

        var page = bookingService.getAllByOffice(office, type, isActual, pageable);

        var dtos = page.stream()
            .map(this::getBookingDto)
            .toList();

        log.info("Page of actual/all bookings of office successfully formed");

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение информации об указанной брони")
    public BookingGetDto getById(@PathVariable Long id) {
        var booking = bookingService.getById(id);
        var bookingDto = getBookingDto(booking);

        log.info("Booking successfully found");

        return bookingDto;
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменение указанной брони")
    public String update(@Valid @RequestBody BookingUpdateDto dto) {

        var maker = getSessionUser();
        var holder = validateHolder(dto.getHolderId());

        var place = validatePlace(dto.getWorkPlaceId());

        var start = dto.getStart();
        validateBookingStart(start);

        var end = dto.getEnd();
        validateByOfficeWorkTime(place, start.toLocalTime(), end.toLocalTime());
        validateIsAlreadyBooked(place, start, end);

        var booking = bookingMapper.dtoToBooking(dto, holder, maker, place);
        bookingService.update(booking);

        log.info("Booking successfully updated");

        return "Бронь успешно обновлена";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление указанной брони")
    public String delete(@PathVariable Long id) {
        bookingService.delete(id);

        log.info("Bookings successfully deleted");

        return "Бронь успешно удалена";
    }

    private Employee getSessionUser() {
        return employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName()
        );
    }

    private Employee validateHolder(long id) {
        var holder = employeeService.getById(id);
        if (holder == null) {
            log.error("Not found a holder of booking by employee id: {}", id);
            throw new NotFoundException("Не найден держатель брони с id: " + id);
        }
        return holder;
    }

    private WorkPlace validatePlace(long id) {
        var place = workPlaceService.getById(id);
        if (place == null) {
            log.error("Not found a place of booking by workPlace id: {}", id);
            throw new NotFoundException("Не найдено место с id: " + id);
        }
        return place;
    }

    private WorkPlace validatePlaceAndCapacity(long id, int count) {
        var place = validatePlace(id);
        if (count > place.getCapacity()) {
            log.error("Conflict: count of people for booking {} more than place capacity {}",
                count, place.getCapacity());
            throw new IncorrectArgumentException(String.format(
                "Невозможно создать бронь т.к. объем бронирований %s превышает вместимость места %s",
                count, place.getCapacity()));
        }
        return place;
    }

    private void validateIsAlreadyBooked(
        WorkPlace place,
        LocalDateTime start,
        LocalDateTime end
    ) {
        boolean isTimeFree = bookingService
            .getAllInPeriod(place, start, end, Pageable.ofSize(1)).isEmpty();

        if (!isTimeFree) {
            log.error("Conflict: cannot add booking — place already booked on this time {}-{}",
                start, end);
            throw new AlreadyExistsException(String.format(
                "Невозможно забронировать данное место на данное время: %s - %s",
                start, end
            ));
        }
    }

    private void validateBookingStart(LocalDateTime start) {
        var now = LocalDateTime.now(UTC);
        if (start.isBefore(now)) {
            log.error("Conflict: count of people for booking {} more than place capacity {}",
                start, now);
            throw new IncorrectArgumentException(String.format(
                "Невозможно создать/изменить бронь на прошедший момент времени: %s < %s",
                start, now));
        }

    }

    private void validateByOfficeWorkTime(WorkPlace place, LocalTime start, LocalTime end) {
        var floor = place.getFloor();
        if (floor == null) {
            log.error("Not found floor in place with id: {}", place.getId());
            throw new NotFoundException("Не найден этаж у места с id: " + place.getId());
        }
        var office = floor.getOffice();
        if (office == null) {
            log.error("Not found office in floor with id: {}, in place with id: {}",
                floor.getId(), place.getId());
            throw new NotFoundException(String.format(
                "У этажа с id %s по месту с id %s не найден офис",
                floor.getId(), place.getId()));
        }

        if (office.getStartOfDay() == null) {
            log.error("Not found work day start of office with id: {}, floor with id: {}, " +
                      "in place with id: {}", office.getId(), floor.getId(), place.getId());
            throw new NotFoundException(String.format(
                "У офиса не найдено начало рабочего дня. Id офиса: %s, Id этажа: %s, Id места: %s",
                office.getId(), floor.getId(), place.getId()));
        }

        if (office.getEndOfDay() == null) {
            log.error("Not found work day end of office with id: {}, floor with id: {}, " +
                      "in place with id: {}", office.getId(), floor.getId(), place.getId());
            throw new NotFoundException(String.format(
                "У офиса не найден конец рабочего дня. Id офиса: %s, Id этажа: %s, Id места: %s",
                office.getId(), floor.getId(), place.getId()));
        }

        if (start.isBefore(office.getStartOfDay())) {
            log.error("Conflict: start of the booking before office work day start: {} < {}",
                start, office.getStartOfDay());
            throw new IncorrectArgumentException(String.format(
                "Начало брони не может быть раньше начала рабочего дня офиса: %s < %s",
                start, office.getStartOfDay()));
        }

        if (end.isAfter(office.getEndOfDay())) {
            log.error("Conflict: end of the booking after office work day end: {} < {}",
                end, office.getEndOfDay());
            throw new IncorrectArgumentException(String.format(
                "Конец брони не может быть позже конца рабочего дня офиса: %s < %s",
                end, office.getEndOfDay()));
        }

    }

    private BookingGetDto getBookingDto(Booking booking) {

        var infoBooking = Optional.of(booking)
            .map(infoMapper::bookingToDto)
            .orElseThrow(() -> new NotFoundException("Не найдена бронь с id: " + booking.getId()));

        var infoPlace = getPlaceInfo(booking);

        var infoOffice = getOfficeInfo(booking);

        return new BookingGetDto(infoBooking, infoPlace, infoOffice);
    }

    private InfoPlaceDto getPlaceInfo(Booking booking) {
        return Optional.of(booking)
            .map(Booking::getWorkPlace)
            .map(infoMapper::placeToDto)
            .orElseThrow(() -> new NotFoundException("Не найдено место у брони с id: " + booking.getId()));
    }

    private InfoOfficeDto getOfficeInfo(Booking booking) {
        return Optional.of(booking)
            .map(Booking::getWorkPlace)
            .map(WorkPlace::getFloor)
            .map(Floor::getOffice)
            .map(infoMapper::officeToDto)
            .orElseThrow(() -> new NotFoundException(String.format(
                "При поиске от брони с id:%s не был найден офис",
                booking.getId())));
    }

}
