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
import plugin.atb.booking.entity.*;
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
    public String createBookingsForTeam(@Valid @RequestBody BookingCreateDto dto) {

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
            .map(TeamMemberEntity::getEmployee)
            .collect(Collectors.toSet());

        var booking = bookingMapper.dtoToBooking(dto, leaderAsHolder, maker, place);
        var newBooking = bookingService.add(booking);

        var conferees = employees.stream()
            .map(e -> new ConferenceMemberEntity().setBooking(newBooking).setEmployee(e))
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
    public String createBookingsForGroup(@Valid @RequestBody BookingGroupCreateDto dto) {

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
            .map(m -> new ConferenceMemberEntity().setBooking(newBooking).setEmployee(m))
            .collect(Collectors.toSet());
        conferenceMemberService.addAll(conferees);
        log.info("Booking and conferees successfully created");

        return "Место успешно забронировано, участники брони созданы";
    }

    @GetMapping("/conference")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить брони с переговорок указанного сотрудника",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> get(
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

        var bookings = page.map(ConferenceMemberEntity::getBooking);

        var dtos = bookings.stream()
            .map(BookingEntity::getId)
            .map(this::getBookingDto)
            .toList();

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
            .map(BookingEntity::getId)
            .map(this::getBookingDto)
            .toList();

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
            .map(BookingEntity::getId)
            .map(this::getBookingDto)
            .toList();

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
            .map(BookingEntity::getId)
            .map(this::getBookingDto)
            .toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/allByHolder/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить брони указанного сотрудника - держателя броней",
        description = "1 <= size <= 20 (default 20)")
    public Page<BookingGetDto> getSelfBookings(
        @PathVariable Long id,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var holder = validateHolder(id);

        var page = bookingService.getHolderHistory(holder, pageable);

        var dtos = page.stream()
            .map(BookingEntity::getId)
            .map(this::getBookingDto)
            .toList();

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
            .map(BookingEntity::getId)
            .map(this::getBookingDto)
            .toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение информации об указанной брони: часть брони + место + офис")
    public BookingGetDto longBooking(@PathVariable Long id) {
        return getBookingDto(id);
    }

    @PutMapping("/")
    @Operation(summary = "Изменение указанной брони")
    public ResponseEntity<String> update(@Valid @RequestBody BookingUpdateDto dto) {

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

    private WorkPlaceEntity validatePlaceAndCapacity(long id, int count) {
        var place = validatePlace(id);
        if (count > place.getCapacity()) {
            throw new IncorrectArgumentException(String.format(
                "Невозможно создать бронь т.к. объем бронирований %s превышает вместимость места %s",
                count, place.getCapacity()));
        }
        return place;
    }

    private void validateIsAlreadyBooked(
        WorkPlaceEntity place,
        LocalDateTime start,
        LocalDateTime end
    ) {
        boolean isTimeFree = bookingService
            .getAllInPeriod(place, start, end, Pageable.ofSize(1)).isEmpty();

        if (!isTimeFree) {
            throw new AlreadyExistsException(String.format(
                "Невозможно забронировать данное место на данное время: %s - %s",
                start, end
            ));
        }
    }

    private void validateBookingStart(LocalDateTime start) {
        var now = LocalDateTime.now(UTC);
        if (start.isBefore(now)) {
            throw new IncorrectArgumentException(String.format(
                "Невозможно создать/изменить бронь на прошедший момент времени: %s < %s",
                start, now));
        }

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

    private BookingGetDto getBookingDto(Long id) {

        var booking = bookingService.getById(id);

        var infoBooking = Optional.of(booking)
            .map(infoMapper::bookingToDto)
            .orElseThrow(() -> new NotFoundException("Не найдена бронь с id: " + id));

        var infoPlace = getPlaceInfo(booking);

        var infoOffice = getOfficeInfo(booking);

        return new BookingGetDto(infoBooking, infoPlace, infoOffice);
    }

    private InfoPlaceDto getPlaceInfo(BookingEntity booking) {
        return Optional.of(booking)
            .map(BookingEntity::getWorkPlace)
            .map(infoMapper::placeToDto)
            .orElseThrow(() -> new NotFoundException("Не найдено место у брони с id: " + booking.getId()));
    }

    private InfoOfficeDto getOfficeInfo(BookingEntity booking) {
        return Optional.of(booking)
            .map(BookingEntity::getWorkPlace)
            .map(WorkPlaceEntity::getFloor)
            .map(FloorEntity::getOffice)
            .map(infoMapper::officeToDto)
            .orElseThrow(() -> new NotFoundException(String.format(
                "При поиске от брони с id:%s не был найден офис",
                booking.getId())));
    }

}
