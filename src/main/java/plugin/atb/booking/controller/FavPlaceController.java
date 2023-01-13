package plugin.atb.booking.controller;

import java.time.*;
import java.util.*;
import java.util.stream.*;

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
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/favPlace")
@Tag(name = "Избранное место", description = "Содержит связь сотрудника и избранного им места")
public class FavPlaceController {

    private final EmployeeService employeeService;

    private final FavPlaceService favPlaceService;

    private final WorkPlaceService workPlaceService;

    private final WorkPlaceMapper workPlaceMapper;

    private final FloorService floorService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление места в избранное пользователем сессии")
    public String add(@RequestParam Long placeId) {
        ValidationUtils.checkId(placeId);
        var employee = getSessionUser();

        var place = validatePlace(placeId);

        favPlaceService.add(new FavPlace()
            .setEmployee(employee)
            .setPlace(place));

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Место успешно добавлено в избранное";
    }

    @GetMapping("/allSelf")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить свои (пользователя сессии) избранные места (DTO рабочего места)")
    public Page<WorkPlaceGetDto> getAllSelf(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);
        var employee = getSessionUser();

        var page = favPlaceService.getAllByEmployee(employee, pageable);

        var dtos = page.stream()
            .map(FavPlace::getPlace)
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allSelfIsFree")
    @Operation(summary = "Получить свои (пользователя сессии) избранные места на этаже " +
                         "для бронирования - т.е. свободные/занятые (DTO места с пометкой о занятости)")
    public List<PlaceAvailabilityResponseDto> getAllSelfIsFree(
        @RequestParam
        Long floorId,
        @RequestParam
        @Parameter(example = "2022-11-13 10:00", description = "Формат: yyyy-MM-dd HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime start,
        @RequestParam
        @Parameter(example = "2022-11-13 22:00", description = "Формат: yyyy-MM-dd HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime end
    ) {
        ValidationUtils.checkId(floorId);

        var employee = getSessionUser();

        var favPlacesPage = favPlaceService.getAllByEmployee(employee, Pageable.unpaged());
        var favPlacesSet = favPlacesPage.stream()
            .map(FavPlace::getPlace)
            .collect(Collectors.toSet());

        var floor = validateFloor(floorId);
        var floorPlacesPage = workPlaceService.getPageByFloor(floor, Pageable.unpaged());

        var floorFavPlaces = new ArrayList<>(floorPlacesPage.getContent());
        floorFavPlaces.retainAll(favPlacesSet);
        if (floorFavPlaces.isEmpty()) {
            return List.of();
        }

        var bookedFavPlaces = workPlaceService.getAllBookedInPeriod(
            floorFavPlaces,
            start,
            end);

        var bookedIds = bookedFavPlaces.stream()
            .map(WorkPlace::getId)
            .collect(Collectors.toSet());

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return floorFavPlaces.stream()
            .map(p -> workPlaceMapper.placeToPlaceAvailabilityDto(
                p,
                !bookedIds.contains(p.getId())))
            .toList();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allOfEmployee/{employeeId}")
    @Operation(summary = "Получить все избранные места указанного сотрудника")
    public ResponseEntity<Page<WorkPlaceGetDto>> getAllByEmployee(
        @PathVariable Long employeeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(employeeId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var employee = validateEmployee(employeeId);

        var page = favPlaceService.getAllByEmployee(employee, pageable);

        var dtos = page.stream()
            .map(FavPlace::getPlace)
            .map(workPlaceMapper::workPlaceToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return ResponseEntity.ok(new PageImpl<>(dtos, page.getPageable(), page.getTotalElements()));
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/self/{placeId}")
    @Operation(summary = "Убрать место по его id из своих (пользователя сессии) избранных")
    public ResponseEntity<String> delete(@PathVariable Long placeId) {

        ValidationUtils.checkId(placeId);

        var employee = getSessionUser();
        var place = validatePlace(placeId);

        favPlaceService.delete(employee, place);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return ResponseEntity.ok("Место успешно удалено из избранного");
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{employeeId}/place/{placeId}")
    @Operation(summary = "Убрать место из избранного по его id и id сотрудника")
    public String delete(@PathVariable Long employeeId, @PathVariable Long placeId) {
        ValidationUtils.checkId(employeeId);
        ValidationUtils.checkId(placeId);

        var employee = validateEmployee(employeeId);
        var place = validatePlace(placeId);

        favPlaceService.delete(employee, place);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Место успешно удалено из избранного";
    }

    private Employee getSessionUser() {
        return employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName()
        );
    }

    private Employee validateEmployee(Long id) {
        var employee = employeeService.getById(id);
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + id);
        }
        return employee;
    }

    private WorkPlace validatePlace(Long id) {
        var place = workPlaceService.getById(id);
        if (place == null) {
            throw new NotFoundException("Не найдено место с id: " + id);
        }
        return place;
    }

    private Floor validateFloor(long id) {
        var floor = floorService.getById(id);
        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }
        return floor;
    }

}
