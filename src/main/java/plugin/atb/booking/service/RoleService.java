package plugin.atb.booking.service;

import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public void add(String name) {
        boolean exists = roleRepository.existsByName(name);

        if (exists) {
            throw new AlreadyExistsException("Роль уже существует: " + name);
        }

        Role role = new Role();
        role.setName(name);
        roleRepository.save(role);
    }

    public List<Role> getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        return roleRepository.findAll(sort);
    }

    public Role getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Не указан id для поиска роли");
        }

        ValidationUtils.checkId(id);

        return roleRepository.findById(id).orElse(null);
    }

    @Transactional
    public void update(Role role) {

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

    @Transactional
    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найдена роль с id: " + id);
        }

        roleRepository.deleteById(id);
    }

}
