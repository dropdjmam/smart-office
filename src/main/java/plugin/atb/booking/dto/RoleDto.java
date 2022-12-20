package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class RoleDto {

    @NotNull(message = "Не указан id роли")
    @Min(value = 1L, message = "Id роли не может быть меньше 1")
    private Long id;

    @NotBlank(message = "Имя роли не может быть пустым или состоять только из пробелов")
    private String name;

}
