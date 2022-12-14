package plugin.atb.booking.dto;

import java.time.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@Setter
public class ExceptionsDto {

    private HttpStatus status;

    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    private Map<String, String> errors;

    public ExceptionsDto(HttpStatus status, Map<String, String> errors) {
        this.status = status;
        this.errors = errors;
    }

}
