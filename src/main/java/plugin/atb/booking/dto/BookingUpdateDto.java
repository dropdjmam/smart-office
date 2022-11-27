package plugin.atb.booking.dto;

import java.time.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingUpdateDto {

    private Long id;

    private Long newWorkPlaceId;

    private LocalDateTime newStart;

    private LocalDateTime newEnd;

    private int guests;

}
