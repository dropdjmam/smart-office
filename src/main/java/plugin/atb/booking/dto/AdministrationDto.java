package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AdministrationDto {

    @NotNull
    private Long id;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long officeId;

}
