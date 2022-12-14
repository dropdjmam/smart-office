package plugin.atb.booking.exception;

import java.util.*;
import java.util.stream.*;

import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.validation.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import plugin.atb.booking.dto.*;

/**
 * Аннотация для swagger-а - @ApiResponse, для handler-ов с одним кодом ошибки и различными ДТО
 * указана над "Первым" методом с данным кодом ошибки. Раздельное написание ApiResponse для
 * таких handler-ов приводит к неверному отображению в swagger-е.
 */

@ControllerAdvice
public class ControllerExceptionHandler {

    @ApiResponse(responseCode = "404", useReturnTypeSchema = true, description = "Объект не найден")
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> notFoundExceptionHandler(NotFoundException ex) {

        var dto = new ExceptionDto(HttpStatus.NOT_FOUND, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }

    @ApiResponse(responseCode = "409", useReturnTypeSchema = true, description = "Объект уже существует")
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ExceptionDto> alreadyExistExceptionHandler(AlreadyExistsException ex) {

        var dto = new ExceptionDto(HttpStatus.CONFLICT, ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(dto);
    }

    @ApiResponse(responseCode = "400",
        description = "Ошибки валидации. Два варианта возвращаемого JSON объекта (смотреть Examples)",
        content = @Content(
            examples = {
                @ExampleObject(name = "Вариант с одной ошибкой",
                    value = "{\"status\": \"BAD_REQUEST\",\"timestamp\": \"08-08-2008 08:08:08\","
                            + "\"message\": \"Что-либо не прошло валидацию.\"}"),
                @ExampleObject(name = "Вариант со списком ошибок",
                    value = "{\"status\": \"BAD_REQUEST\",\"timestamp\": \"08-08-2008 08:08:08\","
                            + "\"errors\": {\"field\": \"Поле field не прошло валидацию.\","
                            + "\"name\": \"Поле name не прошло валидацию.\"}}"),
            },
            schema = @Schema(oneOf = {ExceptionDto.class, ExceptionsDto.class}))
    )
    @ExceptionHandler(IncorrectArgumentException.class)
    public @Schema ResponseEntity<ExceptionDto> incorrectArgumentExceptionHandler(
        IncorrectArgumentException ex
    ) {

        var dto = new ExceptionDto(HttpStatus.BAD_REQUEST, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionsDto> methodArgumentNotValidExceptionHandler(
        MethodArgumentNotValidException ex
    ) {

        var errors = ex.getBindingResult().getFieldErrors();

        var errorsMap = errors.stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                e -> Objects.requireNonNullElse(e.getDefaultMessage(), "Ошибка валидации")));

        var dto = new ExceptionsDto(HttpStatus.BAD_REQUEST, errorsMap);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionDto> methodMissingServletRequestParameterExceptionHandler(
        MissingServletRequestParameterException ex
    ) {

        var dto = new ExceptionDto(HttpStatus.BAD_REQUEST, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler
    ResponseEntity<ExceptionDto> httpMessageNotReadableExceptionHandler(
        HttpMessageNotReadableException ex
    ) {

        var dto = new ExceptionDto(HttpStatus.BAD_REQUEST, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

}
