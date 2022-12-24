package plugin.atb.booking.dto;

import java.time.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class InfoBookingDto {

    private Long id;

    private Long makerId;

    private String makerName;

    private Long holderId;

    private String holderName;

    private LocalDateTime start;

    private LocalDateTime end;

    private Integer guests;

    private Boolean isDeleted;

}
