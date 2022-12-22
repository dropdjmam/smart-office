package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeamMemberDto {

    @NotNull
    private Long id;

    @NotNull
    private Long teamId;

    @NotNull
    private Long employeeId;

}
