package plugin.atb.booking.dto;

import java.time.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class BookingPartInfoDto {

    private Long id;

    private Long makerId;

    private String makerName;

    private Long holderId;

    private String holderName;

    private LocalDateTime start;

    private LocalDateTime end;

}