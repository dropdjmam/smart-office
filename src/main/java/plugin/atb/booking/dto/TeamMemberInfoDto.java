package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeamMemberInfoDto {

    @NotNull
    private Long id;

    @NotNull
    private Long teamId;

    @NotNull
    private Long employeeId;

    @NotBlank
    private String fullName;

    @NotNull
    private String roleName;

}
