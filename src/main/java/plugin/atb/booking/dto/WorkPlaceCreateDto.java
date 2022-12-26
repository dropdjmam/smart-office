package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class WorkPlaceCreateDto {

    private String placeName;

    @NotNull(message = "Не указан id типа места")
    @Min(value = 1L, message = "Id типа места не может быть меньше 1")
    private Long typeId;

    @NotNull(message = "Не указан id этажа")
    @Min(value = 1L, message = "Id этажа не может быть меньше 1")
    private Long floorId;

    @NotNull(message = "Не указана вместимость места")
    @Min(value = 1, message = "Вместимость места не может быть меньше 1")
    private Integer capacity;

}
