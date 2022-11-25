package plugin.atb.booking.service;

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
public class WorkPlaceTypeService {

    private final WorkPlaceTypeRepository workPlaceTypeRepository;

    public void add(String name) {
        boolean exists = workPlaceTypeRepository.existsByName(name);

        if (exists) {
            throw new AlreadyExistsException(
                String.format("Такой тип места уже существует: %s", name));
        }

        WorkPlaceTypeEntity type = new WorkPlaceTypeEntity();
        type.setName(name);
        workPlaceTypeRepository.save(type);
    }

    public List<WorkPlaceTypeEntity> getAll() {
        var sort = Sort.by(ASC, "name");
        return workPlaceTypeRepository.findAll(sort);
    }

    public WorkPlaceTypeEntity getById(Long id) {
        return workPlaceTypeRepository.findById(id).orElse(null);
    }

    public List<WorkPlaceTypeEntity> getAllByName(String name) {
        return workPlaceTypeRepository.findAllByNameContainingOrderByName(name);
    }

    public void updateWorkPlaceType(WorkPlaceTypeEntity workPlaceType) {
        WorkPlaceTypeEntity updateWorkPlaceType = getById(workPlaceType.getId());

        if (updateWorkPlaceType == null) {
            throw new NotFoundException("Тип места не найден.");
        }

        if (workPlaceType.getName() != null) {
            updateWorkPlaceType.setName(workPlaceType.getName());
        }
        workPlaceTypeRepository.save(updateWorkPlaceType);
    }

    public void deleteWorkPlaceType(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(
                String.format("Типа места с данным id не существует: %s", id));
        }

        workPlaceTypeRepository.deleteById(id);
    }

}
