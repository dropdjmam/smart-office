package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeGetDto {

    private Long id;

    private String role;

    private String fullName;

    private String login;

    private String email;

    private String phoneNumber;

    private String photo;

}
