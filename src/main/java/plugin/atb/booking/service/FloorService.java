package plugin.atb.booking.service;

import java.util.*;

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
public class FloorService {

    private final FloorRepository floorRepository;

    private final WorkPlaceService workPlaceService;

    @Transactional
    public Long add(Floor floor) {

        validate(floor);

        boolean exists = floorRepository
            .existsByFloorNumberAndOffice(floor.getFloorNumber(), floor.getOffice());

        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Этаж №%s в офисе %s уже существует", floor.getFloorNumber(), floor.getOffice()));
        }

        return floorRepository.save(floor).getId();
    }

    public Page<Floor> getAllByOffice(Office office, Pageable pageable) {

        if (office == null) {
            throw new IncorrectArgumentException("Офис не указан");
        }

        return floorRepository.findAllByOffice(office, pageable);
    }

    public Floor getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return floorRepository.findById(id).orElse(null);
    }

    @Transactional
    public void update(Floor floor) {

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

    @Transactional
    public void delete(Long id) {

        var floor = getById(id);

        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + id);
        }

        var places = workPlaceService.getPageByFloor(floor, Pageable.unpaged());
        workPlaceService.deleteAll(places.getContent());

        floorRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll(List<Floor> floors) {

        var places = floors.stream()
            .map(f -> workPlaceService.getPageByFloor(f, Pageable.unpaged()))
            .flatMap(Streamable::get)
            .toList();

        workPlaceService.deleteAll(places);

        floorRepository.deleteAll(floors);
    }

    private void validate(Floor floor) {

        if (floor.getOffice() == null) {
            throw new IncorrectArgumentException("Не указан офис");
        }

        if (floor.getFloorNumber() == null) {
            throw new IncorrectArgumentException("Не указан номер этажа");
        }

    }

}
