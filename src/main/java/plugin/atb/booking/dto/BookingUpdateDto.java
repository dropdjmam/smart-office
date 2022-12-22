package plugin.atb.booking.dto;

import java.time.*;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingUpdateDto {

    @NotNull(message = "Не указан id брони")
    @Min(value = 1, message = "Id брони не может быть меньше 1")
    private Long id;

    @NotNull(message = "Не указан id держателя брони")
    @Min(value = 1, message = "Id держателя брони не может быть меньше 1")
    private Long holderId;

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
