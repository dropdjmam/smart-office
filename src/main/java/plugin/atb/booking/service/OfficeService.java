package plugin.atb.booking.service;

import java.time.*;
import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

import static org.springframework.data.domain.Sort.Direction.*;

@Service
@RequiredArgsConstructor
public class OfficeService {

    private final OfficeRepository officeRepository;

    public void addOffice(
        CityEntity city, String address, String workNumber, LocalTime startOfDay,
        LocalTime endOfDay, Integer bookingRange
    ) {
        boolean exists = officeRepository.existsByAddress(address);

        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Оффис с данным адрессом уже существует: %s",
                address));
        }

        officeRepository.save(new OfficeEntity(
            null,
            city,
            address,
            workNumber,
            startOfDay,
            endOfDay,
            bookingRange));
    }

    public List<OfficeEntity> getAll() {
        var sort = Sort.by(ASC, "address");
        return officeRepository.findAll(sort);

    }

    public OfficeEntity getById(Long id) {
        return officeRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Оффиса с данным id не существует: %s", id)));

    }

    public OfficeEntity getByAddress(String address) {
        return officeRepository.findByAddress(address);
    }

    public OfficeEntity getByWorkNumber(String workNumber) {
        return officeRepository.findByWorkNumber(workNumber);
    }

    public void updateOffice(OfficeEntity officeEntity) {
        officeRepository.save(officeEntity);
    }

    public void deleteOffice(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format("Оффиса с данным id не существует: %s", id));
        }

        officeRepository.deleteById(id);
    }

}
