package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class WorkPlaceTypeDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

}
