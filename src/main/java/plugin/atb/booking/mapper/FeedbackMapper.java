package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class FeedbackMapper {

    public Feedback dtoToFeedback(FeedbackCreateDto dto, Employee employee) {
        return new Feedback()
            .setEmployee(employee)
            .setTitle(dto.getTitle())
            .setText(dto.getText());
    }

    public FeedbackGetDto feedbackToDto(Feedback feedback) {
        return new FeedbackGetDto(
            feedback.getId(),
            feedback.getEmployee().getId(),
            feedback.getTimeStamp(),
            feedback.getTitle(),
            feedback.getText());
    }

}
