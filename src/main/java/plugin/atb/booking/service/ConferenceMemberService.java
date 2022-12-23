package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class ConferenceMemberService {

    private final ConferenceMemberRepository conferenceMemberRepository;

    public void add(ConferenceMemberEntity conferee) {

        boolean exists = conferenceMemberRepository.existsByEmployeeAndBooking(
            conferee.getEmployee(), conferee.getBooking());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Участник с id:%s уже зарегестрирован на бронь с id:%s",
                conferee.getEmployee().getId(), conferee.getBooking().getId()));
        }

        if (conferee.getEmployee() == null) {
            throw new IncorrectArgumentException("Сотрудник не указан");
        }

        if (conferee.getBooking() == null) {
            throw new IncorrectArgumentException("Бронирование с не указано");
        }

        conferenceMemberRepository.save(conferee);
    }

    public Page<ConferenceMemberEntity> getAllByBookingId(
        Long bookingId,
        Pageable pageable
    ) {
        return conferenceMemberRepository.findAllByBookingId(bookingId, pageable);
    }

    public ConferenceMemberEntity getByBookingId(Long bookingId) {
        return conferenceMemberRepository.findByBookingId(bookingId);
    }

    public ConferenceMemberEntity getByEmployeeId(Long employeeId) {
        return conferenceMemberRepository.findByEmployeeId(employeeId);
    }

    public Page<ConferenceMemberEntity> getAll(Pageable pageable) {
        return conferenceMemberRepository.findAll(pageable);
    }

    public ConferenceMemberEntity getById(Long id) {
        return conferenceMemberRepository.findById(id).orElse(null);
    }

    public void update(ConferenceMemberEntity conferee) {

        if (getById(conferee.getId()) == null) {
            throw new NotFoundException("Участник конференции не найден.");
        }

        var newConferee = conferee.getEmployee();
        var newBooking = conferee.getBooking();

        boolean exists = conferenceMemberRepository.existsByEmployeeAndBooking(
            newConferee, newBooking);
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Участник с id: %s уже имеет бронь с id: %s",
                conferee.getEmployee().getId(), conferee.getBooking().getId()));
        }

        conferenceMemberRepository.save(conferee);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format(
                "Не найден участник конференции с id: %s", id));
        }

        conferenceMemberRepository.deleteById(id);
    }

}
