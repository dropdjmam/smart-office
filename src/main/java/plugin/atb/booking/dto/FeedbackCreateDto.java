package plugin.atb.booking.dto;

import javax.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class FeedbackCreateDto {

    @Size(max = 255, message = "Длина заголовка превышает дефолтные 255 символов")
    @NotBlank(message = "Заголовок не может быть пустым или состоять только из пробелов")
    private String title;

    @Size(max = 500, message = "Длина сообщения превышает установленные 500 символов")
    @NotBlank(message = "Сообщение не может быть пустым или состоять только из пробелов")
    private String text;

}
