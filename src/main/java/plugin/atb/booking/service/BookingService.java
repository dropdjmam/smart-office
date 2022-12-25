package plugin.atb.booking.service;

import java.time.*;

import static java.time.ZoneOffset.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.data.util.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;

    private final ConferenceMemberService conferenceMemberService;

    @Transactional
    public Booking add(Booking booking) {

        validate(booking);

        return bookingRepository.save(booking);
    }

    public Page<Booking> getAllInPeriod(
        WorkPlace place,
        LocalDateTime start,
        LocalDateTime end,
        Pageable pageable
    ) {
        if (place == null) {
            throw new IncorrectArgumentException("Не указано рабочее место для поиска броней");
        }

        if (start == null) {
            throw new IncorrectArgumentException("Не указано начало брони");
        }

        if (end == null) {
            throw new IncorrectArgumentException("Не указан конец брони");
        }

        ValidationUtils.checkInterval(start, end);

        return bookingRepository.findAllInPeriod(place, start, end, pageable);
    }

    public Page<Booking> getAllActual(Employee holder, Pageable pageable) {

        if (holder == null) {
            throw new IncorrectArgumentException("Держатель брони не указан");
        }

        return bookingRepository.findAllActual(holder, pageable);
    }

    public Page<Booking> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public Page<Booking> getHolderHistory(Employee holder, Pageable pageable) {
        if (holder == null) {
            throw new IncorrectArgumentException("Не указан держатель брони");
        }
        return bookingRepository.findAllByHolderAndIsDeletedIsFalse(holder, pageable);
    }

    public Page<Booking> getAllByOffice(
        Office office,
        WorkPlaceType type,
        Boolean isActual,
        Pageable pageable
    ) {
        if (office == null) {
            throw new IncorrectArgumentException("Не указан офис для поиска броней офиса");
        }

        if (isActual == null) {
            throw new IncorrectArgumentException(
                "Не указан масштаб поиска броней офиса - актуальные/все");
        }

        if (type == null) {
            throw new IncorrectArgumentException(
                "Не указан тип места у искомых броней офиса");
        }

        return bookingRepository.findAllByOffice(office, type, isActual, pageable);
    }

    public Booking getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return bookingRepository.findById(id).orElse(null);
    }

    @Transactional
    public void update(Booking booking) {

        var bookingToUpdate = getById(booking.getId());

        if (bookingToUpdate == null) {
            throw new NotFoundException("Не найдена бронь с id: " + booking.getId());
        }

        if (bookingToUpdate.getIsDeleted()) {
            throw new IncorrectArgumentException("Невозможно обновить удаленную бронь");
        }

        var start = bookingToUpdate.getDateTimeOfStart();
        if (start == null) {
            throw new NotFoundException("Не найдено начало у брони с id: " + bookingToUpdate.getId());
        }
        var now = LocalDateTime.now(UTC);
        if (start.isBefore(now)) {
            throw new IncorrectArgumentException(String.format(
                "Невозможно изменить начавшуюся/прошедшую бронь: %s < %s",
                start, now));
        }

        validate(booking);

        bookingRepository.save(booking);
    }

    @Transactional
    public void delete(Long id) {

        var booking = getById(id);
        if (booking == null) {
            throw new NotFoundException("Не найдено бронирование с id: " + id);
        }

        var conferenceMembers = conferenceMemberService.getAllByBookingId(id, Pageable.unpaged());
        if (!conferenceMembers.isEmpty()) {
            conferenceMemberService.deleteAll(conferenceMembers.getContent());
        }

        booking.setIsDeleted(true);

        bookingRepository.save(booking);
    }

    @Transactional
    public void deleteAllByWorkplace(WorkPlace place) {

        var deletedBookings = bookingRepository.deleteAllByWorkPlace(place);

        var conferenceMembers = deletedBookings.stream()
            .map(b -> conferenceMemberService.getAllByBookingId(b.getId(), Pageable.unpaged()))
            .flatMap(Streamable::get)
            .toList();
        if (!conferenceMembers.isEmpty()) {
            conferenceMemberService.deleteAll(conferenceMembers);
        }

    }

    @Transactional
    public void deleteAllByHolder(Employee holder) {
        var deletedBookings = bookingRepository.deleteAllByHolder(holder);

        var conferenceMembers = deletedBookings.stream()
            .map(b -> conferenceMemberService.getAllByBookingId(b.getId(), Pageable.unpaged()))
            .flatMap(Streamable::get)
            .toList();
        if (!conferenceMembers.isEmpty()) {
            conferenceMemberService.deleteAll(conferenceMembers);
        }
    }

    private void validate(Booking booking) {

        if (booking.getIsDeleted() == null) {
            throw new IncorrectArgumentException(
                "Невозможно добавить/обновить бронь, т.к. не указано ее состояние об удалении");
        }

        if (booking.getIsDeleted()) {
            throw new IncorrectArgumentException("Невозможно добавить/обновить бронь как удаленную");
        }

        if (booking.getHolder() == null) {
            throw new IncorrectArgumentException("Не указан держатель брони");
        }

        if (booking.getMaker() == null) {
            throw new IncorrectArgumentException("Не указан создатель брони");
        }

        if (booking.getGuests() == null) {
            throw new IncorrectArgumentException("Не указано количество гостей");
        }

        if (booking.getGuests() < 0) {
            throw new IncorrectArgumentException("Гостей не может быть меньше нуля");
        }

    }

}
