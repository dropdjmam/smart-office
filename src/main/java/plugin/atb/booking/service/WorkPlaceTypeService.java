package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkPlaceTypeService {

    private final WorkPlaceTypeRepository workPlaceTypeRepository;

    @Transactional
    public void add(String name) {
        boolean exists = workPlaceTypeRepository.existsByName(name);

        if (exists) {
            throw new AlreadyExistsException(
                String.format("Такой тип места уже существует: %s", name));
        }

        WorkPlaceType type = new WorkPlaceType();
        type.setName(name);
        workPlaceTypeRepository.save(type);
    }

    public Page<WorkPlaceType> getAll(Pageable pageable) {

        return workPlaceTypeRepository.findAll(pageable);
    }

    public WorkPlaceType getById(Long id) {
        return workPlaceTypeRepository.findById(id).orElse(null);
    }

    public Page<WorkPlaceType> getByName(String name, Pageable pageable) {
        return workPlaceTypeRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional
    public void update(WorkPlaceType type) {
        String newName = type.getName();

        boolean exists = workPlaceTypeRepository.existsByName(newName);

        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Тип места уже существует: %s", newName));
        }

        WorkPlaceType updateType = getById(type.getId());

        if (updateType == null) {
            throw new NotFoundException(String.format(
                "Тип места не найден: %s", type.getName()));
        }

        if (type.getName() != null) {
            updateType.setName(type.getName());
        }

        workPlaceTypeRepository.save(type);
    }

    @Transactional
    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(
                String.format("Типа места с данным id не существует: %s", id));
        }

        workPlaceTypeRepository.deleteById(id);
    }

}
