package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeamDto {

    @NotNull(message = "Не указан id команды")
    @Min(value = 1L, message = "Id команды не может быть меньше 1")
    private Long id;

    @NotNull(message = "Не указан id сотрудника - лидера команды")
    @Min(value = 1L, message = "Id сотрудника - лидера команды, не может быть меньше 1")
    private Long leaderId;

    @NotBlank(message = "Имя команды не может быть пустым или состоять только из пробелов")
    private String name;

}
