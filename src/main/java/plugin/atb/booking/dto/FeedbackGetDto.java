package plugin.atb.booking.dto;

import java.time.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class FeedbackGetDto {

    private Long id;

    private Long employeeId;

    private LocalDateTime timeStamp;

    private String title;

    private String text;

}
