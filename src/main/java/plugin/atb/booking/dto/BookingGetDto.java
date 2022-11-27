package plugin.atb.booking.dto;

import java.time.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingGetDto {

    private Long id;

    private Long holderId;

    private Long makerId;

    private Long workplaceId;

    private LocalDateTime start;

    private LocalDateTime end;

    private int guests;

}
