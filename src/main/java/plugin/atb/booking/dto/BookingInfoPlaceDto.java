package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingInfoPlaceDto {

    private Long placeId;

    private String type;

    private Integer floorNumber;

}
