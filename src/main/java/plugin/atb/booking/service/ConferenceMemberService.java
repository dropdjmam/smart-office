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
                "Участник уже зарегестрирован на бронь: %s",
                conferee.getEmployee()));
        }

        if (conferee.getEmployee() == null) {
            throw new NotFoundException(String.format(
                "Сотрудник не найден: %s", conferee.getEmployee()));
        }

        if (conferee.getBooking() == null) {
            throw new NotFoundException(String.format(
                "Бронирование не найдено: %s", conferee.getBooking()));
        }

        conferenceMemberRepository.save(conferee);
    }

    public Page<ConferenceMemberEntity> getAllByBookingId(Long bookingId, Pageable pageable) {
        return conferenceMemberRepository.findAllByBookingId(bookingId, pageable);
    }

    public Page<ConferenceMemberEntity> getAll(Pageable pageable) {
        return conferenceMemberRepository.findAll(pageable);
    }

    public ConferenceMemberEntity getById(Long id) {
        return conferenceMemberRepository.findById(id).orElse(null);
    }

    public Page<ConferenceMemberEntity> getAllByEmployeeId(Long employeeId, Pageable pageable) {
        return conferenceMemberRepository.findAllByEmployeeId(employeeId, pageable);
    }

    public void update(ConferenceMemberEntity conferee) {
        ConferenceMemberEntity updateConferenceMember = getById(conferee.getId());

        if (updateConferenceMember == null) {
            throw new NotFoundException(String.format(
                "Не найден участник переговоров с id: %s", conferee.getId()));
        }

        if (conferee.getEmployee() != null) {
            updateConferenceMember.setEmployee(conferee.getEmployee());
        }

        if (conferee.getBooking() != null) {
            updateConferenceMember.setBooking(conferee.getBooking());
        }

        conferenceMemberRepository.save(conferee);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format(
                "Не найден участник переговоров с id: %s", id));
        }

        conferenceMemberRepository.deleteById(id);
    }

}
