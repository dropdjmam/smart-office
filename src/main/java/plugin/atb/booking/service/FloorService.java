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

    public Long add(FloorEntity floor) {

        validate(floor);

        boolean exists = floorRepository
            .existsByFloorNumberAndOffice(floor.getFloorNumber(), floor.getOffice());

        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Этаж №%s в офисе %s уже существует", floor.getFloorNumber(), floor.getOffice()));
        }

        return floorRepository.save(floor).getId();
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

        ValidationUtils.checkId(floor.getId());

        if (floor.getId() == null) {
            throw new IncorrectArgumentException("Не указан id этажа");
        }

        if (getById(floor.getId()) == null) {
            throw new NotFoundException("Не найден этаж с id: " + floor.getId());
        }

        validate(floor);

        floorRepository.save(floor);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }

        floorRepository.deleteById(id);
    }

    private void validate(FloorEntity floor) {

        if (floor.getOffice() == null) {
            throw new IncorrectArgumentException("Не указан офис");
        }

        if (floor.getFloorNumber() == null) {
            throw new IncorrectArgumentException("Не указан номер этажа");
        }

    }

}
