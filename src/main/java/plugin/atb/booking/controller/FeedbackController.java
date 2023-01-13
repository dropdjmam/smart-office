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
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать отзыв", description = "Ограничения по длине: title - 255, text - 500")
    public String add(@Valid @RequestBody FeedbackCreateDto dto) {

        var employee = employeeService.getByLogin(
            SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        var feedback = feedbackMapper.dtoToFeedback(dto, employee);
        feedbackService.add(feedback);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Отзыв успешно создан";
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все отзывы", description = "1 <= size <= 20 (default 20)")
    public Page<FeedbackGetDto> getAll(@ParameterObject Pageable pageable) {

        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var page = feedbackService.getAll(pageable);

        var dtos = page.stream()
            .map(feedbackMapper::feedbackToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allOfEmployee/{employeeId}")
    @Operation(summary = "Получить все отзывы указанного сотрудника",
        description = "1 <= size <= 20 (default 20)")
    public Page<FeedbackGetDto> getAllByEmployeeId(
        @PathVariable Long employeeId,
        @ParameterObject Pageable pageable
    ) {
        ValidationUtils.checkId(employeeId);
        ValidationUtils.checkPageSize(pageable.getPageSize(), 20);

        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + employeeId);
        }

        var page = feedbackService.getAllByEmployee(employee, pageable);

        var dtos = page.stream()
            .map(feedbackMapper::feedbackToDto)
            .toList();

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить указанный отзыв")
    public FeedbackGetDto getById(@PathVariable Long id) {

        var feedback = feedbackService.getById(id);
        if (feedback == null) {
            throw new NotFoundException("Не найден отзыв с id: " + id);
        }

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return feedbackMapper.feedbackToDto(feedback);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить указанный отзыв")
    public String delete(@PathVariable Long id) {

        ValidationUtils.checkId(id);

        feedbackService.delete(id);

        log.info("Operation successful, method {}", TraceUtils.getMethodName(1));
        return "Отзыв успешно удален";
    }

}
