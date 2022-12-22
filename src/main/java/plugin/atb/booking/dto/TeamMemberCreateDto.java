package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeamMemberCreateDto {

    @NotNull
    private Long teamId;

    @NotNull
    private Long employeeId;

}
