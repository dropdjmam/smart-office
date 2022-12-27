package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class WorkPlaceUpdateDto {

    @NotNull(message = "Не указан id места")
    @Min(value = 1L, message = "Id места не может быть меньше 1")
    private Long id;

    @NotBlank(message = "Наименование места не может быть пустым или состоять только из пробелов")
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
