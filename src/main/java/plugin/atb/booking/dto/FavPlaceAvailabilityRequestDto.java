package plugin.atb.booking.dto;

import java.time.*;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.*;
import lombok.*;
import org.springframework.format.annotation.*;

@Getter
@Setter
@AllArgsConstructor
public class FavPlaceAvailabilityRequestDto {

    @NotNull(message = "Не указан id этажа")
    @Min(value = 1L, message = "Id этажа не может быть меньше 1")
    @Parameter(example = "1")
    private Long floorId;

    @NotNull(message = "Не указано начало интервала")
    @Parameter(example = "2022-11-13 10:00", description = "Формат: yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime start;

    @NotNull(message = "Не указан конец интервала")
    @Parameter(example = "2022-11-13 22:00", description = "Формат: yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime end;

}
