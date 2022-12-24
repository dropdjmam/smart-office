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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ApiResponse(responseCode = "404", useReturnTypeSchema = true, description = "Объект не найден")
    public ExceptionDto notFoundExceptionHandler(NotFoundException ex) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AlreadyExistsException.class)
    @ApiResponse(responseCode = "409", useReturnTypeSchema = true, description = "Объект уже существует")
    public ExceptionDto alreadyExistExceptionHandler(AlreadyExistsException ex) {
        return new ExceptionDto(HttpStatus.CONFLICT, ex.getMessage());
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectArgumentException.class)
    public ExceptionDto incorrectArgumentExceptionHandler(IncorrectArgumentException ex) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ExceptionsDto methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {

        var errors = ex.getBindingResult().getFieldErrors();

        var errorsMap = errors.stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                e -> Objects.requireNonNullElse(e.getDefaultMessage(), "Ошибка валидации")));

        return new ExceptionsDto(HttpStatus.BAD_REQUEST, errorsMap);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    ExceptionDto methodMissingServletRequestParameterExceptionHandler(
        MissingServletRequestParameterException ex
    ) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ExceptionDto httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

}
