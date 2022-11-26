package plugin.atb.booking.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeUpdateDto {

    private Long id;

    private Long roleId;

    private String fullName;

    private String password;

    private String email;

    private String phoneNumber;

    private String photo;

}
