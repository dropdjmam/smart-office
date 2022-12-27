package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class WorkPlaceTypeDto {

    @NotNull(message = "Не указан id типа места")
    @Min(value = 1L, message = "Id типа места не может быть меньше 1")
    private Long id;

    @NotBlank(message = "Имя типа места не может быть пустым или состоять только из пробелов")
    private String name;

}
