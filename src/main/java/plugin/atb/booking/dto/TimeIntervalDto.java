package plugin.atb.booking.dto;

import java.time.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TimeIntervalDto {

    private LocalTime start;

    private LocalTime end;

}
