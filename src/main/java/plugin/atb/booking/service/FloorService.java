package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class FloorService {

    private final FloorRepository floorRepository;

    public void add(FloorEntity floor) {
        boolean exists = floorRepository
            .existsByFloorNumberAndOffice(floor.getFloorNumber(), floor.getOffice());

        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Этаж №%s в офисе $s уже существует", floor.getFloorNumber(), floor.getOffice()));
        }

        floorRepository.save(floor);
    }

    public Page<FloorEntity> getAllByOfficeId(Long officeId, Pageable pageable) {
        return floorRepository.findAllByOfficeId(officeId, pageable);
    }

    public FloorEntity getById(Long id) {
        return floorRepository.findById(id).orElse(null);
    }

    public void update(FloorEntity floor) {
        FloorEntity updateFloor = getById(floor.getId());

        if (updateFloor == null) {
            throw new NotFoundException("Этаж не найден");
        }

        if (floor.getOffice() != null) {
            updateFloor.setOffice(floor.getOffice());
        }

        if (floor.getFloorNumber() != null) {
            updateFloor.setFloorNumber(floor.getFloorNumber());
        }

        if (floor.getMapFloor() != null) {
            updateFloor.setMapFloor(floor.getMapFloor());
        }

        floorRepository.save(updateFloor);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }

        floorRepository.deleteById(id);
    }

}
