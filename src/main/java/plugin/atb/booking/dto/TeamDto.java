package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TeamDto {

    private Long id;

    @NotNull
    private Long leaderId;

    @NotBlank
    private String name;

}
