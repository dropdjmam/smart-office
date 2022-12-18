package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class FloorCreateDto {

    @NotNull(message = "Не указан id офиса")
    @Min(value = 1L, message = "Id офиса не может быть меньше 1")
    private Long officeId;

    @NotNull(message = "Не указан номер этажа")
    private Integer floorNumber;

    private String mapFloor;

}
