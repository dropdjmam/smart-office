package plugin.atb.booking.controller;

import javax.validation.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springdoc.api.annotations.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
@Tag(name = "Обратная связь/отзыв",
    description = "Время создания отзыва устанавливается в БД с временной зоной 'UTC'")
public class FeedbackController {

    private final FeedbackService feedbackService;

    private final EmployeeService employeeService;

    private final FeedbackMapper feedbackMapper;

    @PostMapping("/")
    @Operation(summary = "Создание отзыва", description = "Ограничения по длине: title - 255, text - 500")
    public ResponseEntity<String> add(@Valid @RequestBody FeedbackCreateDto dto) {

        var employee = employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        var feedback = feedbackMapper.dtoToFeedback(dto, employee);
        feedbackService.add(feedback);

        return ResponseEntity.ok("Отзыв успешно создан");
    }

    @GetMapping("/all")
    @Operation(summary = "Получение всех отзывов", description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<FeedbackGetDto>> getAll(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = feedbackService.getAll(pageable);

        var dtos = page.stream()
            .map(feedbackMapper::feedbackToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dtos, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/allOfEmployee/{employeeId}")
    @Operation(summary = "Получение всех отзывов указанного сотрудника",
        description = "1 <= size <= 20 (default 20)")
    public ResponseEntity<Page<FeedbackGetDto>> getAllByEmployeeId(
        @PathVariable Long employeeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(employeeId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            log.error("IN add METHOD - Not found employee by id: " + employeeId);
            throw new NotFoundException("Не найден сотрудник с id: " + employeeId);
        }

        var page = feedbackService.getAllByEmployee(employee, pageable);

        var dtos = page.stream()
            .map(feedbackMapper::feedbackToDto)
            .toList();

        return ResponseEntity.ok(new PageImpl<>(dtos, page.getPageable(), page.getTotalElements()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение указанного отзыва")
    public ResponseEntity<FeedbackGetDto> getById(@PathVariable Long id) {

        var feedback = feedbackService.getById(id);
        if (feedback == null) {
            log.error("IN getById METHOD - Not found feedback by id: " + id);
            throw new NotFoundException("Не найден отзыв с id: " + id);
        }

        var dto = feedbackMapper.feedbackToDto(feedback);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление отзыва")
    public ResponseEntity<String> delete(@PathVariable Long id) {

        ValidationUtils.checkId(id);

        feedbackService.delete(id);

        return ResponseEntity.ok("Отзыв успешно удален");
    }

}