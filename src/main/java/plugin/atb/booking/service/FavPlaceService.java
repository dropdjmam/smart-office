package plugin.atb.booking.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavPlaceService {

    private final FavPlaceRepository favPlaceRepository;

    @Transactional
    public void add(FavPlace favPlace) {

        if(favPlace.getEmployee() == null){
            log.error("IN add METHOD — no employee {}", favPlace);
            throw new IncorrectArgumentException("Сотрудник не указан");
        }

        if(favPlace.getPlace() == null){
            log.error("IN add METHOD — no place {}", favPlace);
            throw new IncorrectArgumentException("Место не указано");
        }

        boolean exists = favPlaceRepository.existsByEmployeeAndPlace(
            favPlace.getEmployee(),
            favPlace.getPlace());

        if (exists) {
            log.error("IN add METHOD — {} already exists", favPlace);
            throw new AlreadyExistsException(String.format(
                "Место с id: %s, уже является избранным у сотрудника с id: %s",
                favPlace.getPlace().getId(), favPlace.getEmployee().getId()));
        }

        favPlaceRepository.save(favPlace);

        log.info("IN add METHOD — {} successfully added", favPlace);
    }

    public Page<FavPlace> getAllByEmployee(Employee employee, Pageable pageable) {

        if (employee == null) {
            log.error("IN getAllByEmployee METHOD - employee is null");
            throw new IncorrectArgumentException("Не указан сотрудник");
        }
        var page = favPlaceRepository.findAllByEmployee(employee, pageable);

        log.info("IN getAllByEmployee METHOD - {} elements found", page.getTotalElements());

        return page;
    }

    @Transactional
    public void delete(Employee employee, WorkPlace place) {

        boolean exists = favPlaceRepository.existsByEmployeeAndPlace(employee, place);

        if (!exists) {
            log.error("IN delete METHOD - Not found {} as fav of employee with id: {}",
                place, employee.getId());
            throw new NotFoundException(String.format(
                "Место с id: %s не найдено в избранных сотрудника с id: %s",
                place.getId(), employee.getId()));
        }

        favPlaceRepository.deleteByEmployeeAndPlace(employee, place);

        log.info("Successfully deleted {} from fav of employee with id: {}",
            place, employee.getId());
    }

}
