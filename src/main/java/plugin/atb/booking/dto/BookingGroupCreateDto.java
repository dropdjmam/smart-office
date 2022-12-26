package plugin.atb.booking.dto;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Schema(example = "{\"holderIds\": [1,2],\"workPlaceId\": 1," +
                  "\"start\":\"2022-12-25T14:00:00.000+10:00[Asia/Vladivostok]\"," +
                  "\"end\": \"2022-12-25T18:00:00.000+10:00[Asia/Vladivostok]\",\"guests\": 0}")
public class BookingGroupCreateDto {

    @Size(min = 2, max = 20,
        message = "Ограничение количества сотрудников для брони: 2~20")
    @NotEmpty(message = "Не указан ни один держатель брони")
    private List<
        @NotNull(message = "Не указан id держателя брони")
        @Min(value = 1, message = "Id держателя брони не может быть меньше 1")
            Long> holderIds;

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
