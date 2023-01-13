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
import org.springframework.security.access.prepost.*;
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
@RequestMapping("/workplace")
@Tag(name = "Рабочее место", description = "Места офиса, нахождение свободных/занятых мест, а также " +
                                           "свободных интервалов для бронирования данных мест")
public class WorkPlaceController {

    private final WorkPlaceTypeService workPlaceTypeService;

    private final FloorService floorService;

    private final WorkPlaceService workPlaceService;

    private final WorkPlaceMapper workPlaceMapper;

    private final BookingService bookingService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Создание рабочего места",
        description = "Все поля кроме имени обязательны, имя по дефолту \"№ \"{newId}\"\"")
    public Long createWorkPlace(@Valid @RequestBody WorkPlaceCreateDto dto) {

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

        var newPlace = workPlaceMapper.dtoToWorkPlace(dto, type, floor);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return workPlaceService.add(newPlace);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/countOnFloor/{floorId}")
    @Operation(summary = "Количество мест каждого типа на указанный этаж")
    public CountPlaceDto getPlacesCountByFloor(@PathVariable Long floorId) {

        var floor = validateFloor(floorId);
        var commonType = validateType(1);
        var conferenceType = validateType(2);

        var commonAmount = workPlaceService.countPlacesByTypeAndFloor(commonType, floor);
        var conferenceAmount = workPlaceService.countPlacesByTypeAndFloor(conferenceType, floor);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new CountPlaceDto(commonAmount, conferenceAmount);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Все рабочие места", description = "1 <= size <= 20 (default 20)")
    public Page<WorkPlaceGetDto> getPage(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = workPlaceService.getPage(pageable);

        var dto = page.stream()
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/allByFloor")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить рабочие места указанного типа на указанном этаже",
        description = "1 <= size <= 20 (default 20)")
    public Page<WorkPlaceGetDto> getPageByFloor(
        @RequestParam Long floorId, @RequestParam Long typeId, @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(floorId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var type = validateType(typeId);

        var floor = validateFloor(floorId);

        var page = workPlaceService.getPageByFloorAndType(floor, type, pageable);

        var dto = page.stream()
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allIsFreeByFloor")
    @Operation(summary = "Все места одного типа на указанном этаже помеченные как занятые/свободные",
        description = "Для запроса необходим id этажа, id типа места, " +
                      "начало временного интервала и конец, а также " +
                      "параметры пагинации. 1 <= size <= 20 (default 20)")
    public Page<PlaceAvailabilityResponseDto> getIsFreeByFloor(
        @Valid @ModelAttribute @ParameterObject PlaceAvailabilityRequestDto dto,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

        var floorPlaces = workPlaceService.getPageByFloorAndType(floor, type, pageable);

        if (floorPlaces.isEmpty()) {
            return Page.empty(floorPlaces.getPageable());
        }

        var office = floor.getOffice();
        if (office == null) {
            throw new NotFoundException(String.format(
                "Не найден офис по этажу с id: %s",
                floor.getId()
            ));
        }

        var zoneId = ZoneId.of(office.getCity().getZoneId());
        var bookedPlaces = workPlaceService.getAllBookedInPeriod(
            floorPlaces.getContent(),
            dto.getStart().atZone(zoneId).withZoneSameInstant(UTC).toLocalDateTime(),
            dto.getEnd().atZone(zoneId).withZoneSameInstant(UTC).toLocalDateTime());

        var bookedIds = bookedPlaces.stream()
            .map(WorkPlace::getId)
            .collect(Collectors.toSet());

        var responseDtos = floorPlaces.stream()
            .map(p -> workPlaceMapper.placeToPlaceAvailabilityDto(
                p,
                !bookedIds.contains(p.getId())))
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(
            responseDtos,
            floorPlaces.getPageable(),
            floorPlaces.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/freeIntervals")
    @Operation(summary = "Получить свободные промежутки для бронирования места",
        description = "Свободные промежутки составляются с учетом рабочего времени офиса")
    public List<TimeIntervalDto> getFreeIntervalsForPlace(
        @RequestParam Long placeId,
        @Parameter(example = "2022-11-13", description = "Формат: yyyy-MM-dd")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        @RequestParam LocalDate date
    ) {
        ValidationUtils.checkId(placeId);

        var place = workPlaceService.getById(placeId);
        if (place == null) {
            throw new NotFoundException("Не найдено рабочее место с id: " + placeId);
        }

        var floor = place.getFloor();
        if (floor == null) {
            throw new NotFoundException("Не найден этаж у места с id: " + placeId);
        }

        var office = floor.getOffice();
        if (office == null) {
            throw new NotFoundException(String.format(
                "Не найден офис по этажу с id: %s, по указанному месту с id: %s ",
                floor.getId(), placeId
            ));
        }

        var range = office.getBookingRange();
        if (range == null) {
            throw new NotFoundException("Не найдено ограничение брони для офиса с id: " + office.getId());
        }

        var isDateNotInRange = date.isAfter(LocalDate.now().plusDays(range));

        if (isDateNotInRange) {
            throw new IncorrectArgumentException(String.format(
                "Дата брони выходит за ограничения офиса. Дальность брони: %s дней",
                office.getBookingRange()));
        }

        if (office.getStartOfDay() == null || office.getEndOfDay() == null) {
            throw new NotFoundException("Не найден интервал работы офиса с id: " + office.getId());
        }

        var workDayStart = office.getStartOfDay();
        var workDayEnd = office.getEndOfDay();

        var zoneId = ZoneId.of(office.getCity().getZoneId());
        var bookingsPage = bookingService.getAllInPeriod(
            place,
            LocalDateTime.of(date, workDayStart)
                .atZone(zoneId)
                .withZoneSameInstant(UTC)
                .toLocalDateTime(),
            LocalDateTime.of(date, workDayEnd)
                .atZone(zoneId)
                .withZoneSameInstant(UTC)
                .toLocalDateTime(),
            Pageable.unpaged());

        var bookings = bookingsPage.getContent();

        if (bookings.isEmpty()) {
            log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
            return List.of(new TimeIntervalDto(workDayStart, workDayEnd));
        }

        var size = bookings.size();

        var starts = bookings.stream()
            .map(s -> s.getDateTimeOfStart().atZone(UTC).withZoneSameInstant(zoneId).toLocalTime())
            .toList();

        var ends = bookings.stream()
            .map(s -> s.getDateTimeOfEnd().atZone(UTC).withZoneSameInstant(zoneId).toLocalTime())
            .toList();

        var response = IntStream.range(1, size)
            .mapToObj(i -> new TimeIntervalDto(ends.get(i - 1), starts.get(i)))
            .collect(Collectors.toList());

        if (!workDayStart.equals(starts.get(0))) {
            response.add(0, new TimeIntervalDto(workDayStart, starts.get(0)));
        }

        if (!workDayEnd.equals(ends.get(size - 1))) {
            response.add(size, new TimeIntervalDto(ends.get(size - 1), workDayEnd));
        }

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return response;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить указанное места")
    public WorkPlaceGetDto getWorkPlaceById(@PathVariable Long id) {
        var workPlace = workPlaceService.getById(id);
        if (workPlace == null) {
            throw new NotFoundException("Не найдено место с id: " + id);
        }

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return workPlaceMapper.workPlaceToDto(workPlace);
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Изменить указанное место", description = "Все поля обязательны")
    public String update(@Valid @RequestBody WorkPlaceUpdateDto dto) {

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

        workPlaceService.update(workPlaceMapper.dtoToWorkPlace(dto, type, floor));

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Место успешно обновлено";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Удалить указанное место")
    public String delete(@PathVariable Long id) {

        workPlaceService.delete(id);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Место успешно удалено";
    }

    private Floor validateFloor(long id) {
        var floor = floorService.getById(id);
        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }
        return floor;
    }

    private WorkPlaceType validateType(long id) {
        var type = workPlaceTypeService.getById(id);
        if (type == null) {
            throw new NotFoundException("Не найден тип места с id: " + id);
        }
        return type;
    }

}
