package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

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

    public Page<WorkPlaceTypeEntity> getAll(Pageable pageable) {

        return workPlaceTypeRepository.findAll(pageable);
    }

    public WorkPlaceTypeEntity getById(Long id) {
        return workPlaceTypeRepository.findById(id).orElse(null);
    }

    public Page<WorkPlaceTypeEntity> getByName(String name, Pageable pageable) {
        return workPlaceTypeRepository.findByName(name, pageable);
    }

    public void update(WorkPlaceTypeEntity type) {
        String newName = type.getName();

        boolean exists = workPlaceTypeRepository.existsByName(newName);

        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Тип места уже существует: %s", newName));
        }

        WorkPlaceTypeEntity updateType = getById(type.getId());

        if (updateType == null) {
            throw new NotFoundException(String.format(
                "Тип места не найден: %s", type.getName()));
        }

        if (type.getName() != null) {
            updateType.setName(type.getName());
        }

        workPlaceTypeRepository.save(type);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(
                String.format("Типа места с данным id не существует: %s", id));
        }

        workPlaceTypeRepository.deleteById(id);
    }

}
