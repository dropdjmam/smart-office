package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class PlaceAvailabilityResponseDto {

    private Long id;

    private String placeName;

    private String typeName;

    private Long floorId;

    private Integer capacity;

    private Boolean isFree;

}
