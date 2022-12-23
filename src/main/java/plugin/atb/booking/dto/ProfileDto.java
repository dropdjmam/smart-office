package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDto {

    private EmployeeGetDto employee;

    private BookingGetDto firstBooking;

    private TeamDto firstTeam;

}
