package plugin.atb.booking.dto;

import java.time.*;
import java.util.*;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
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
    private LocalDateTime start;

    @NotNull(message = "Не указан конец интервала")
    private LocalDateTime end;

    @NotNull(message = "Не указано количество гостей")
    @Min(value = 0, message = "Количество гостей не может быть меньше 0")
    private Integer guests;

}
