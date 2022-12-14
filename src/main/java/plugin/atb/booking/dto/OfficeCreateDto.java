package plugin.atb.booking.dto;

import java.time.*;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class OfficeCreateDto {

    @NotNull(message = "Не указан id города")
    @Min(value = 1L, message = "Id города не может быть меньше 1")
    private Long cityId;

    @NotBlank(message = "Адрес не может быть пустым или состоять только из пробелов")
    private String address;

    @NotNull(message = "Не указан рабочий телефон")
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$",
        message = "Формат телефона не прошел валидацию, убедитесь что ввели все верно")
    private String workNumber;

    @NotNull(message = "Не указано начало рабочего дня")
    @Schema(implementation = String.class, format = "hh:mm", example = "00:00")
    private LocalTime startOfDay;

    @NotNull(message = "Не указан конец рабочего дня")
    @Schema(implementation = String.class, format = "hh:mm", example = "00:00")
    private LocalTime endOfDay;

    @NotNull(message = "Не указано ограничение дальности брони")
    @Min(value = 1, message = "Ограничение дальность брони не может быть меньше 1")
    private Integer bookingRange;

}
