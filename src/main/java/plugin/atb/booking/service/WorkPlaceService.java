package plugin.atb.booking.service;

import java.time.*;
import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
public class WorkPlaceService {

    private final WorkPlaceRepository workPlaceRepository;

    private final BookingService bookingService;

    public Long add(WorkPlace workPlace) {

        validate(workPlace);

        return workPlaceRepository.save(workPlace).getId();
    }

    public Integer countPlacesByTypeAndFloor(WorkPlaceType type, Floor floor) {
        if (type == null) {
            throw new IncorrectArgumentException("Не указан тип места");
        }
        return workPlaceRepository.countAllByTypeAndFloor(type, floor);
    }

    public Page<WorkPlace> getPage(Pageable pageable) {
        return workPlaceRepository.findAll(pageable);
    }

    public Page<WorkPlace> getPageByFloorAndType(
        Floor floor,
        WorkPlaceType type,
        Pageable pageable
    ) {

        if (floor == null) {
            throw new IncorrectArgumentException("Этаж не указан");
        }

        if (type == null) {
            throw new IncorrectArgumentException("Тип места не указан");
        }

        return workPlaceRepository.findAllByFloorAndType(floor, type, pageable);
    }

    public Page<WorkPlace> getPageByFloor(
        Floor floor,
        Pageable pageable
    ) {

        if (floor == null) {
            throw new IncorrectArgumentException("Этаж не указан");
        }

        return workPlaceRepository.findAllByFloor(floor, pageable);
    }

    public List<WorkPlace> getAllBookedInPeriod(
        List<WorkPlace> floorPlaces,
        LocalDateTime start,
        LocalDateTime end
    ) {

        if (floorPlaces.isEmpty()) {
            throw new IncorrectArgumentException("Места этажа не указаны");
        }

        if (start == null) {
            throw new IncorrectArgumentException("Начало интервала не указано");
        }

        if (end == null) {
            throw new IncorrectArgumentException("Конец интервала не указан");
        }

        if (start.isAfter(end)) {
            throw new IncorrectArgumentException(String.format(
                "Начало интервала не может быть позже конца: %s < %s",
                end, start));
        }

        return workPlaceRepository.findAllBookedInPeriod(floorPlaces, start, end);
    }

    public WorkPlace getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return workPlaceRepository.findById(id).orElse(null);
    }

    public void update(WorkPlace workPlace) {

        if (getById(workPlace.getId()) == null) {
            throw new NotFoundException("Не найдено место с id: " + workPlace.getId());
        }

        validate(workPlace);

        workPlaceRepository.save(workPlace);
    }

    @Transactional
    public void delete(Long id) {

        var place = getById(id);

        if (place == null) {
            throw new NotFoundException("Не найдено место с id: " + id);
        }

        bookingService.deleteAllByWorkplace(place);

        workPlaceRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll(List<WorkPlace> places) {

        places.forEach(bookingService::deleteAllByWorkplace);

        workPlaceRepository.deleteAll(places);
    }

    private void validate(WorkPlace workPlace) {

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
