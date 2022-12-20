package plugin.atb.booking.service;

import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public void add(String name) {
        boolean exists = roleRepository.existsByName(name);

        if (exists) {
            throw new AlreadyExistsException("Роль уже существует: " + name);
        }

        RoleEntity role = new RoleEntity();
        role.setName(name);
        roleRepository.save(role);
    }

    public List<RoleEntity> getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        return roleRepository.findAll(sort);
    }

    public RoleEntity getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Не указан id для поиска роли");
        }

        ValidationUtils.checkId(id);

        return roleRepository.findById(id).orElse(null);
    }

    public void update(RoleEntity role) {

        if (role.getName().isBlank()) {
            throw new IncorrectArgumentException("Не указано имя роли");
        }

        boolean exists = roleRepository.existsByName(role.getName());

        if (exists) {
            throw new AlreadyExistsException(
                "Роль уже существует: " + role.getName());
        }

        roleRepository.save(role);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найдена роль с id: " + id);
        }

        roleRepository.deleteById(id);
    }

}
