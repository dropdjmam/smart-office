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
        return roleRepository.findById(id).orElse(null);
    }

    public List<RoleEntity> getAllByName(String name) {
        return roleRepository.findAllByNameContainingOrderByName(name);
    }

    public void update(RoleEntity role) {
        String newName = role.getName();

        boolean exists = roleRepository.existsByName(newName);

        if (exists) {
            throw new AlreadyExistsException(
                "Роль уже существует: " + newName);
        }

        RoleEntity roleUpdate = getById(role.getId());

        if (roleUpdate == null) {
            throw new NotFoundException("Не найдена роль с id: " + role.getId());
        }

        roleUpdate.setName(newName);

        roleRepository.save(roleUpdate);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Не найдена роль с id: " + id);
        }

        roleRepository.deleteById(id);
    }

}
