package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CityDto {

    @NotNull(message = "Не указан id города")
    @Min(value = 1L, message = "Id города не может быть меньше 1")
    private Long id;

    @NotBlank(message = "Имя города не может быть пустым или состоять только из пробелов")
    private String name;

    @NotBlank(message = "Строка тайм зоны не может быть пустой или состоять только из пробелов")
    private String zoneId;

}
