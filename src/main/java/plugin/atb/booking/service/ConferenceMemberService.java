package plugin.atb.booking.service;

import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConferenceMemberService {

    private final ConferenceMemberRepository conferenceMemberRepository;

    @Transactional
    public void add(ConferenceMember conferee) {

        if (conferee.getEmployee() == null) {
            throw new IncorrectArgumentException("Сотрудник не указан");
        }

        if (conferee.getBooking() == null) {
            throw new IncorrectArgumentException("Бронирование не указано");
        }

        boolean exists = conferenceMemberRepository.existsByEmployeeAndBooking(
            conferee.getEmployee(), conferee.getBooking());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Сотрудник с id:%s уже относится к брони с id:%s",
                conferee.getEmployee().getId(), conferee.getBooking().getId()));
        }

        conferenceMemberRepository.save(conferee);
    }

    @Transactional
    public void addAll(Set<ConferenceMember> conferees) {

        conferenceMemberRepository.saveAll(conferees);
    }

    public Page<ConferenceMember> getAllByBookingId(
        Long bookingId, Pageable pageable
    ) {
        if (bookingId == null) {
            throw new IncorrectArgumentException("Id брони не указан");
        }

        ValidationUtils.checkId(bookingId);

        return conferenceMemberRepository.findAllByBookingId(bookingId, pageable);
    }

    public Page<ConferenceMember> getAllActualByEmployee(
        Employee employee, Pageable pageable
    ) {
        if (employee == null) {
            throw new IncorrectArgumentException("Не указан сотрудник ");
        }

        return conferenceMemberRepository.findAllActualByEmployee(employee, pageable);
    }

    public Page<ConferenceMember> getAll(Pageable pageable) {
        return conferenceMemberRepository.findAll(pageable);
    }

    public ConferenceMember getById(Long id) {
        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return conferenceMemberRepository.findById(id).orElse(null);
    }

    @Transactional
    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format(
                "Не найден участник конференции с id: %s", id));
        }

        conferenceMemberRepository.deleteById(id);
    }

    @Transactional
    public void delete(List<ConferenceMember> conferenceMembers) {
        conferenceMemberRepository.deleteAll(conferenceMembers);
    }

}
