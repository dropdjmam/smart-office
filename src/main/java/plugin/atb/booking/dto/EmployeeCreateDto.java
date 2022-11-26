package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeCreateDto {

    private Long roleId;

    @NotBlank(message = "Имя не может быть пустым или состоять только из пробелов")
    private String fullName;

    @NotBlank(message = "Логин не может быть пустым или состоять только из пробелов")
    private String login;

    @Pattern(
        regexp = "^\\w{8,}",
        message = "Пароль должен быть не менее 8 символов и может " +
                  "содержать только буквы, цифры и знак нижнего подчеркивания."
    )
    private String password;

    private String email;

    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$")
    private String phoneNumber;

    private String photo;

}
