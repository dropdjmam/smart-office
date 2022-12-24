package plugin.atb.booking.controller;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@Tag(name = "Рабочее место")
@RestController
@RequiredArgsConstructor
@RequestMapping("/workplace")
public class WorkPlaceController {

    private final WorkPlaceTypeService workPlaceTypeService;

    private final FloorService floorService;

    private final WorkPlaceService workPlaceService;

    private final WorkPlaceMapper workPlaceMapper;

    private final BookingService bookingService;

    @PostMapping("/")
    @Operation(summary = "Создание рабочего места", description = "Все поля обязательны")
    public ResponseEntity<Long> createWorkPlace(@Valid @RequestBody WorkPlaceCreateDto dto) {

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

        var newId = workPlaceService.add(workPlaceMapper.dtoToWorkPlace(
            type,
            floor,
            dto.getCapacity()));

        return ResponseEntity.ok(newId);
    }

    @GetMapping("/countOnFloor/{floorId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Количество мест каждого типа на указанный этаж")
    public CountPlaceDto getPlacesCountByFloor(@PathVariable Long floorId) {

        var floor = validateFloor(floorId);
        var commonType = validateType(1);
        var conferenceType = validateType(2);

        var commonAmount = workPlaceService.countPlacesByTypeAndFloor(commonType, floor);
        var conferenceAmount = workPlaceService.countPlacesByTypeAndFloor(conferenceType, floor);

        return new CountPlaceDto(commonAmount, conferenceAmount);
    }

    @GetMapping("/all")
    @Operation(summary = "Все рабочие места", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<WorkPlaceGetDto>> getPage(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = workPlaceService.getPage(pageable);

        var dto = page.stream()
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/allByFloor")
    @Operation(summary = "Все рабочие места на указанном этаже", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<WorkPlaceGetDto>> getPageByFloor(
        @RequestParam Long floorId,
        @RequestParam Long typeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(floorId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var type = validateType(typeId);

        var floor = validateFloor(floorId);

        var page = workPlaceService.getPageByFloorAndType(floor, type, pageable);

        var dto = page.stream()
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dto, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/allIsFreeByFloor")
    @Operation(summary = "Все места одного типа на указанном этаже помеченные как занятые/свободные",
        description = "Для запроса необходим id этажа, id типа места, " +
                      "начало временного интервала и конец, а также " +
                      "параметры пагинации. 1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<PlaceAvailabilityResponseDto>> getIsFreeByFloor(
        @Valid @ModelAttribute @ParameterObject PlaceAvailabilityRequestDto dto,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

        var floorPlaces = workPlaceService.getPageByFloorAndType(floor, type, pageable);

        if (floorPlaces.isEmpty()) {
            return ResponseEntity.ok(Page.empty(floorPlaces.getPageable()));
        }

        var bookedPlaces = workPlaceService.getAllBookedInPeriod(
            floorPlaces.getContent(),
            dto.getStart(),
            dto.getEnd());

        var bookedIds = bookedPlaces.stream()
            .map(WorkPlace::getId)
            .collect(Collectors.toSet());

        var responseDtos = floorPlaces.stream()
            .map(p -> workPlaceMapper.placeToPlaceAvailabilityDto(
                p,
                !bookedIds.contains(p.getId())))
            .toList();

        return ResponseEntity.ok(new PageImpl<>(
            responseDtos,
            floorPlaces.getPageable(),
            floorPlaces.getTotalElements()));
    }

    @GetMapping("/freeIntervals")
    @Operation(summary = "Получение свободных промежутков для бронирования места",
        description = "Свободные промежутки составляются с учетом рабочего времени офиса.")
    public ResponseEntity<List<TimeIntervalDto>> getFreeIntervalsForPlace(
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

        var bookingsPage = bookingService.getAllInPeriod(
            place,
            LocalDateTime.of(date, workDayStart),
            LocalDateTime.of(date, workDayEnd),
            Pageable.unpaged());

        var bookings = bookingsPage.getContent();

        if (bookings.isEmpty()) {
            return ResponseEntity.ok(List.of(new TimeIntervalDto(workDayStart, workDayEnd)));
        }

        var size = bookings.size();

        var starts = bookings.stream()
            .map(s -> s.getDateTimeOfStart().toLocalTime())
            .toList();

        var ends = bookings.stream()
            .map(s -> s.getDateTimeOfEnd().toLocalTime())
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

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение указанного места")
    public ResponseEntity<WorkPlaceGetDto> getWorkPlaceById(@PathVariable Long id) {

        var workPlace = workPlaceService.getById(id);
        if (workPlace == null) {
            throw new NotFoundException("Не найдено место с id: " + id);
        }

        return ResponseEntity.ok(workPlaceMapper.workPlaceToDto(workPlace));
    }

    @PutMapping("/")
    @Operation(summary = "Изменение указанного места", description = "Все поля обязательны")
    public ResponseEntity<String> update(@Valid @RequestBody WorkPlaceUpdateDto dto) {

        var type = validateType(dto.getTypeId());

        var floor = validateFloor(dto.getFloorId());

        workPlaceService.update(workPlaceMapper.dtoToWorkPlace(dto, type, floor));

        return ResponseEntity.ok("Место успешно обновлено");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление указанного места")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        workPlaceService.delete(id);

        return ResponseEntity.ok("Место успешно удалено");
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
