package plugin.atb.booking.dto;

import java.time.*;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Schema(example = "{\"holderId\": 1,\"workPlaceId\": 1," +
                  "\"start\":\"2022-12-25T14:00:00.000+10:00[Asia/Vladivostok]\"," +
                  "\"end\": \"2022-12-25T18:00:00.000+10:00[Asia/Vladivostok]\",\"guests\": 0}")
public class BookingCreateDto {

    @NotNull(message = "Не указан id держателя брони")
    @Min(value = 1, message = "Id держателя брони не может быть меньше 1")
    private Long holderId;

    @NotNull(message = "Не указан id места")
    @Min(value = 1, message = "Id места не может быть меньше 1")
    private Long workPlaceId;

    @NotNull(message = "Не указано начало интервала")
    private ZonedDateTime start;

    @NotNull(message = "Не указан конец интервала")
    private ZonedDateTime end;

    @NotNull(message = "Не указано количество гостей")
    @Min(value = 0, message = "Количество гостей не может быть меньше 0")
    private Integer guests;

}
