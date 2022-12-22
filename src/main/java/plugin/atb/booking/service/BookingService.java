package plugin.atb.booking.service;

import java.time.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;

    @Transactional
    public void add(BookingEntity booking) {

        validate(booking);

        var start = booking.getDateTimeOfStart();
        var end = booking.getDateTimeOfEnd();

        boolean isTimeFree = bookingRepository
            .findAllInPeriod(booking.getWorkPlace(), start, end, Pageable.ofSize(1)).isEmpty();

        if (!isTimeFree) {
            throw new AlreadyExistsException(String.format(
                "Невозможно забронировать данное место на данное время: %s - %s",
                start, end
            ));
        }

        bookingRepository.save(booking);

    }

    public Page<BookingEntity> getAllInPeriod(
        WorkPlaceEntity place,
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

        if (end.isBefore(start)) {
            throw new IncorrectArgumentException(String.format(
                "Конец брони не может быть раньше начала: %s < %s",
                end, start
            ));
        }

        return bookingRepository.findAllInPeriod(place, start, end, pageable);
    }

    public Page<BookingEntity> getAllActual(EmployeeEntity holder, Pageable pageable) {

        if (holder == null) {
            throw new IncorrectArgumentException("Держатель брони не указан");
        }

        return bookingRepository.findAllActual(holder, pageable);
    }

    public Page<BookingEntity> getAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public BookingEntity getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return bookingRepository.findById(id).orElse(null);
    }

    @Transactional
    public void update(BookingEntity booking) {

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
        var now = LocalDateTime.now();
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

        booking.setIsDeleted(true);

        bookingRepository.save(booking);
    }

    private void validate(BookingEntity booking) {

        if (booking.getIsDeleted() == null) {
            throw new IncorrectArgumentException(
                "Невозможно добавить/обновить бронь, т.к. не указано ее состояние об удалении");
        }

        if (booking.getIsDeleted()) {
            throw new IncorrectArgumentException("Невозможно добавить/обновить бронь как удаленную");
        }

        var place = booking.getWorkPlace();
        if (place == null) {
            throw new IncorrectArgumentException("Место для брони не указано");
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

        var start = booking.getDateTimeOfStart();
        if (start == null) {
            throw new IncorrectArgumentException("Не указано начало брони");
        }

        var now = LocalDateTime.now();
        if (start.isBefore(now)) {
            throw new IncorrectArgumentException(String.format(
                "Невозможно создать/изменить бронь на прошедший момент времени: %s < %s",
                start, now));
        }

        var end = booking.getDateTimeOfEnd();
        if (end == null) {
            throw new IncorrectArgumentException("Не указан конец брони");
        }

        if (end.isBefore(start)) {
            throw new IncorrectArgumentException(String.format(
                "Конец брони не может быть раньше начала: %s < %s",
                end, start
            ));
        }

        var capacity = place.getCapacity();
        if (capacity == null) {
            throw new IncorrectArgumentException(String.format(
                "Невозможно добавить/изменить бронь, т.к. у места для брони с id:%s не указана вместимость",
                place.getId()));
        }

        var page = getAllInPeriod(place, start, end, Pageable.unpaged());
        var count = page.getTotalElements();
        var allMembers = count + booking.getGuests();

        if (allMembers > capacity) {
            throw new IncorrectArgumentException(String.format(
                "Невозможно забронировать место, т.к. оно не вмещает больше участников: %s > %s",
                allMembers, capacity));
        }

    }

}
