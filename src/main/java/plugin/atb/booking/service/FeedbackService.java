package plugin.atb.booking.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public void add(Feedback feedback) {

        validate(feedback);

        feedbackRepository.save(feedback);

        log.info("New feedback added {}", feedback);

    }

    public Page<Feedback> getAll(Pageable pageable) {
        var page = feedbackRepository.findAll(pageable);

        log.info("IN getAll METHOD - {} elements found", page.getTotalElements());

        return page;
    }

    public Page<Feedback> getAllByEmployee(Employee employee, Pageable pageable) {
        if (employee == null) {
            log.error("IN getAllByEmployee METHOD - employee is null");
            throw new IncorrectArgumentException("Не указан сотрудник");
        }

        var page = feedbackRepository.findAllByEmployee(employee, pageable);

        log.info("IN getAllByEmployee METHOD - {} elements found", page.getTotalElements());

        return page;
    }

    public Feedback getById(Long id) {

        if (id == null) {
            log.warn("IN getById METHOD - id is null");
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        var feedback = feedbackRepository.findById(id).orElse(null);

        log.info("Found: {}", feedback);

        return feedback;
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            log.error("IN delete METHOD - Not found feedback by id: " + id);
            throw new NotFoundException("Не найдена обратная связь с id: " + id);
        }

        feedbackRepository.deleteById(id);

        log.info("Удалена обратная связь с id: " + id);
    }

    private void validate(Feedback feedback) {

        if (feedback.getEmployee() == null) {
            log.warn("Validation. Employee is null: {}", feedback);
            throw new IncorrectArgumentException("Не указан сотрудник");
        }

        if (feedback.getTitle().isBlank()) {
            log.warn("Validation. Title is blank: {}", feedback);
            throw new IncorrectArgumentException(
                "Заголовок не может быть пустым или состоять только из пробелов");
        }

        if (feedback.getText().isBlank()) {
            log.warn("Validation. Text is blank: {}", feedback);
            throw new IncorrectArgumentException(
                "Текст не может быть пустым или состоять только из пробелов");
        }

    }

}
