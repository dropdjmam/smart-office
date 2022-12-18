package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class WorkPlaceGetDto {

    private Long id;

    private String typeName;

    private Long floorId;

    private Integer capacity;

}
