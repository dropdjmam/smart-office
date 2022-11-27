package plugin.atb.booking.dto;

import java.time.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingCreateDto {

    private Long holderId;

    private Long workPlaceId;

    private LocalDateTime start;

    private LocalDateTime end;

    private int guests;

}
