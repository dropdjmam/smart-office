package plugin.atb.booking.service;

import java.time.*;
import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final int PAGE_LIMIT = 8;

    private final BookingRepository bookingRepository;

    public void add(BookingEntity booking) {

        if (booking.getWorkPlace() == null) {
            throw new NotFoundException("Место для брони не найдено");
        }

        LocalDateTime start = booking.getDateTimeOfStart();
        LocalDateTime end = booking.getDateTimeOfEnd();

        boolean validTimeInterval = end.isAfter(start);

        if (!validTimeInterval) {
            throw new IllegalArgumentException(String.format(
                "Конец брони не может быть раньше начала: %s < %s",
                end, start
            ));
        }

        boolean bookingsInTime = bookingRepository
            .findAllInPeriod(booking.getWorkPlace(), start, end).isEmpty();

        if (!bookingsInTime) {
            throw new AlreadyExistsException(String.format(
                "Невозможно забронировать данное место на данное время: %s - %s",
                start, end
            ));
        }

        if (booking.getGuests() < 0) {
            throw new IllegalArgumentException("Гостей не может быть меньше нуля");
        }

        bookingRepository.save(booking);

    }

    public List<BookingEntity> getAllInPeriod(
        WorkPlaceEntity workPlace,
        LocalDateTime start,
        LocalDateTime end
    ) {
        return bookingRepository.findAllInPeriod(workPlace, start, end);
    }

    public List<BookingEntity> getAllActual(Long id) {
        return bookingRepository.findAllActual(id);
    }

    public List<BookingEntity> getPage(Integer pageNumber) {
        Sort sort = Sort.by(Sort.Direction.DESC, "dateTimeOfStart");

        PageRequest request = PageRequest.of(pageNumber, PAGE_LIMIT, sort);

        Page<BookingEntity> page = bookingRepository.findAll(request);

        if (page.isEmpty()) {
            return new ArrayList<>();
        }
        return page.getContent();
    }

    public BookingEntity getById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public void update(BookingEntity booking) {
        BookingEntity updateBooking = getById(booking.getId());

        if (updateBooking == null) {
            throw new NotFoundException("Бронирование не найдено");
        }

        if (booking.getWorkPlace() != null) {
            updateBooking.setWorkPlace(booking.getWorkPlace());
        }

        if (booking.getMaker() != null) {
            updateBooking.setMaker(booking.getMaker());
        }

        if (booking.getDateTimeOfStart() != null) {
            updateBooking.setDateTimeOfStart(booking.getDateTimeOfStart());
        }

        if (booking.getDateTimeOfEnd() != null) {
            updateBooking.setDateTimeOfEnd(booking.getDateTimeOfEnd());
        }

        if (booking.getGuests() > 0) {
            updateBooking.setGuests(booking.getGuests());
        }

        bookingRepository.save(updateBooking);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найдено бронирование с id: " + id);
        }

        bookingRepository.deleteById(id);
    }

}
