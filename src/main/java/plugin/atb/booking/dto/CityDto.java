package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CityDto {

    private Long id;

    @NotBlank
    private String name;

}
