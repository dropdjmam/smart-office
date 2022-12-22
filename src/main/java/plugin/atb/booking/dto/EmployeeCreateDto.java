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

    @NotNull(message = "Не указан пароль")
    @Pattern(
        regexp = "^\\w{8,}$",
        message = "Пароль должен быть не менее 8 символов и может " +
                  "содержать только буквы, цифры и знак нижнего подчеркивания."
    )
    private String password;

    @NotBlank(message = "Почта не может быть пустой или состоять только из пробелов")
    private String email;

    @NotNull(message = "Не указан номер телефона")
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$",
        message = "Формат телефона не прошел валидацию, убедитесь что ввели все верно")
    private String phoneNumber;

}
