package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeamMemberCreateDto {

    @NotNull(message = "Не указан id команды")
    @Min(value = 1L, message = "Id команды не может быть меньше 1")
    private Long teamId;

    @NotNull(message = "Не указан id сотрудника - нового участника команды")
    @Min(value = 1L, message = "Id сотрудника - нового участника команды не может быть меньше 1")
    private Long employeeId;

}
