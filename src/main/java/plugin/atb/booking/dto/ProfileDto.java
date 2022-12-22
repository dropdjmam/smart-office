package plugin.atb.booking.dto;

import java.util.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDto {

    private EmployeeGetDto employee;

    private List<BookingGetDto> bookings;

    private List<TeamDto> teams;

}
