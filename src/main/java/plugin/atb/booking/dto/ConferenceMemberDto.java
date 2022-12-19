package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ConferenceMemberDto {

    @NotBlank
    private Long id;

    @NotBlank
    private Long employeeId;

    @NotBlank
    private Long bookingId;

}
