package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
public class OfficeService {

    private final OfficeRepository officeRepository;

    public Long add(OfficeEntity office) {

        boolean exists = officeRepository.existsByAddress(office.getAddress());

        if (exists) {
            throw new AlreadyExistsException(
                "Офис со следующим адресом уже существует: " + office.getAddress());
        }

        validate(office);

        return officeRepository.save(office).getId();
    }

    public Page<OfficeEntity> getAll(Pageable pageable) {

        return officeRepository.findAll(pageable);
    }

    public OfficeEntity getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return officeRepository.findById(id).orElse(null);
    }

    public Page<OfficeEntity> getAllByAddress(String address, Pageable pageable) {

        if (address.isBlank()) {
            throw new IncorrectArgumentException(
                "Адрес не может быть пустым или состоять только из пробелов");
        }

        return officeRepository.findAllByAddressContaining(address, pageable);
    }

    public void update(OfficeEntity office) {

        if (getById(office.getId()) == null) {
            throw new NotFoundException("Не найден офис с id: " + office.getId());
        }

        validate(office);

        officeRepository.save(office);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найден офис с id: " + id);
        }

        ValidationUtils.checkId(id);

        officeRepository.deleteById(id);
    }

    private void validate(OfficeEntity office) {

        if (office.getCity() == null) {
            throw new IncorrectArgumentException("Город, в котором должен находиться офис не указан");
        }

        var start = office.getStartOfDay();
        if (start == null) {
            throw new IncorrectArgumentException("Начало рабочего дня не указано");
        }

        var end = office.getEndOfDay();
        if (end == null) {
            throw new IncorrectArgumentException("Конец рабочего дня не указан");
        }

        if (office.getBookingRange() == null) {
            throw new IncorrectArgumentException("Ограничения дальности брони не указано");
        }

        if (start.isAfter(end)) {
            throw new IncorrectArgumentException(String.format(
                "Начало рабочего дня не может быть позже конца рабочего дня: %s > %s",
                start, end));
        }

        if (office.getBookingRange() < 1) {
            throw new IncorrectArgumentException(
                "Ограничение дальности бронирования не может составлять меньше 1 дня");
        }
    }

}
