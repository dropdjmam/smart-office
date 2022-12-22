package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class FeedbackMapper {

    public FeedbackEntity dtoToFeedback(FeedbackCreateDto dto, EmployeeEntity employee) {
        var feedback = new FeedbackEntity()
            .setEmployee(employee)
            .setTitle(dto.getTitle())
            .setText(dto.getText());

        return feedback;
    }

    public FeedbackGetDto feedbackToDto(FeedbackEntity feedback) {
        var dto = new FeedbackGetDto(
            feedback.getId(),
            feedback.getEmployee().getId(),
            feedback.getTimeStamp(),
            feedback.getTitle(),
            feedback.getText()
        );
        return dto;
    }

}
