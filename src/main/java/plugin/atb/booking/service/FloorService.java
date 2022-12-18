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
public class FloorService {

    private final FloorRepository floorRepository;

    public void add(FloorEntity floor) {

        validate(floor);

        boolean exists = floorRepository
            .existsByFloorNumberAndOffice(floor.getFloorNumber(), floor.getOffice());

        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Этаж №%s в офисе %s уже существует", floor.getFloorNumber(), floor.getOffice()));
        }

        floorRepository.save(floor);
    }

    public Page<FloorEntity> getAllByOffice(OfficeEntity office, Pageable pageable) {

        if (office == null) {
            throw new IncorrectArgumentException("Офис не указан");
        }

        return floorRepository.findAllByOffice(office, pageable);
    }

    public FloorEntity getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return floorRepository.findById(id).orElse(null);
    }

    public void update(FloorEntity floor) {

        validate(floor);

        if (getById(floor.getId()) == null) {
            throw new NotFoundException("Не найден этаж с id: " + floor.getId());
        }

        floorRepository.save(floor);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }

        floorRepository.deleteById(id);
    }

    private void validate(FloorEntity floor) {

        if (floor == null) {
            throw new IncorrectArgumentException("Этаж не указан");
        }

        if (floor.getId() == null) {
            throw new IncorrectArgumentException("Не указан id этажа");
        }

        ValidationUtils.checkId(floor.getId());

        if (floor.getOffice() == null) {
            throw new IncorrectArgumentException("Не указан офис");
        }

        if (floor.getFloorNumber() == null) {
            throw new IncorrectArgumentException("Не указан номер этажа");
        }

    }

}
