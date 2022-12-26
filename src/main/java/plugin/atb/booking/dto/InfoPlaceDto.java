package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class InfoPlaceDto {

    private Long placeId;

    private String placeName;

    private String type;

    private Integer floorNumber;

}
