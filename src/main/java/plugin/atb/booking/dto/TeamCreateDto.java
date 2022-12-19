package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeamCreateDto {

    @NotNull
    private Long leaderId;

    @NotBlank
    private String name;

}
