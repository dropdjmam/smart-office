package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkPlaceTypeDto {

    private Long id;

    @NotBlank
    private String name;

}
