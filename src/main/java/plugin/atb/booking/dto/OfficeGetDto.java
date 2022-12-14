package plugin.atb.booking.dto;

import java.time.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class OfficeGetDto {

    private Long id;

    private String cityName;

    private String address;

    private String workNumber;

    private LocalTime startOfDay;

    private LocalTime endOfDay;

    private Integer bookingRange;

}
