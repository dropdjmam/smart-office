package plugin.atb.booking.service;

import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class WorkPlaceService {

    private static final int PAGE_LIMIT = 10;

    private final WorkPlaceRepository workPlaceRepository;

    public void add(WorkPlaceEntity workPlace) {
        if (workPlace.getFloor() == null) {
            throw new NotFoundException("Этаж расположения места не найден.");
        }
        if (workPlace.getCapacity() < 1) {
            throw new IllegalArgumentException(
                "Вместимость рабочего места не может быть меньше 1!"
            );
        }

        if (workPlace.getType() == null) {
            throw new NotFoundException("Тип рабочего места не найден.");
        }
        workPlaceRepository.save(workPlace);
    }

    public List<WorkPlaceEntity> getPage(Integer pageNumber) {
        var sort = Sort.by(Sort.Direction.ASC, "id");
        return workPlaceRepository.findAll(sort);
    }

    public List<WorkPlaceEntity> getAllByFloor(Integer pageNumber, FloorEntity floor) {
        PageRequest request = PageRequest.of(pageNumber, PAGE_LIMIT);

        Page<WorkPlaceEntity> workPlaces;
        workPlaces = workPlaceRepository.findAllByFloorOrderByFloor(request, floor);

        if (workPlaces.isEmpty()) {
            return new ArrayList<>();
        }
        return workPlaces.getContent();
    }

    public WorkPlaceEntity getById(Long id) {
        return workPlaceRepository.findById(id).orElse(null);

    }

    public void update(WorkPlaceEntity workPlace) {
        WorkPlaceEntity updateWorkPlace = getById(workPlace.getId());

        if (updateWorkPlace == null) {
            throw new NotFoundException(
                "Рабочее место с id " + workPlace.getId() + " не найдено."
            );
        }

        if (workPlace.getFloor() != null) {
            updateWorkPlace.setFloor(workPlace.getFloor());
        }

        if (workPlace.getType() != null) {
            updateWorkPlace.setType(workPlace.getType());
        }

        if (workPlace.getCapacity() > 0) {
            updateWorkPlace.setCapacity(workPlace.getCapacity());
        }

        workPlaceRepository.save(workPlace);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Рабочее место с id " + id + " не найдено!");
        }

        workPlaceRepository.deleteById(id);
    }

}
