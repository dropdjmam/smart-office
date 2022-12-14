package plugin.atb.booking.dto;

import java.time.*;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@Setter
public class ExceptionDto {

    private HttpStatus status;

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    private String message;

    public ExceptionDto(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
