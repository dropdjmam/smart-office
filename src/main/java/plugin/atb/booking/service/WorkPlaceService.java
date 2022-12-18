package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class WorkPlaceService {

    private final WorkPlaceRepository workPlaceRepository;

    public void add(WorkPlaceEntity workPlace) {

        validate(workPlace);

        workPlaceRepository.save(workPlace);
    }

    public Page<WorkPlaceEntity> getPage(Pageable pageable) {
        return workPlaceRepository.findAll(pageable);
    }

    public Page<WorkPlaceEntity> getPageByFloor(FloorEntity floor, Pageable pageable) {

        if (floor == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        return workPlaceRepository.findAllByFloor(floor, pageable);
    }

    public WorkPlaceEntity getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        if (id < 1) {
            throw new IncorrectArgumentException("Id не может быть меньше 1");
        }

        return workPlaceRepository.findById(id).orElse(null);
    }

    public void update(WorkPlaceEntity workPlace) {

        if (getById(workPlace.getId()) == null) {
            throw new NotFoundException("Не найдено место с id: " + workPlace.getId());
        }

        validate(workPlace);

        workPlaceRepository.save(workPlace);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найдено место с id: " + id);
        }

        workPlaceRepository.deleteById(id);
    }

    private void validate(WorkPlaceEntity workPlace) {

        if (workPlace.getType() == null) {
            throw new IncorrectArgumentException("Не указан тип места");
        }

        if (workPlace.getCapacity() == null) {
            throw new IncorrectArgumentException("Не указана вместимость места");
        }

        if (workPlace.getCapacity() < 1) {
            throw new IncorrectArgumentException("Вместимость места не может быть меньше 1");
        }

        if (workPlace.getType().getId() == 1 && workPlace.getCapacity() != 1) {
            throw new IncorrectArgumentException("Вместимость одиночного места не равна 1");
        }

        if (workPlace.getType().getId() != 1 && workPlace.getCapacity() == 1) {
            throw new IncorrectArgumentException("Вместимость не одиночного места равна 1");
        }

        if (workPlace.getFloor() == null) {
            throw new IncorrectArgumentException("Этаж расположения места не указан");
        }
    }

}
