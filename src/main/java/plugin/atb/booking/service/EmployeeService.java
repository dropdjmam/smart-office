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
public class EmployeeService {

    private static final int PAGE_LIMIT = 8;

    private final EmployeeRepository employeeRepository;

    public void add(EmployeeEntity employee) {

        boolean exists = employeeRepository.existsEmployeeByLoginOrEmail(
            employee.getLogin(),
            employee.getEmail()
        );

        if (exists) {
            throw new AlreadyExistsException(
                "Пользователь с таким логином или почтой уже существует: " +
                employee.getLogin() + ", " + employee.getEmail()
            );
        }

        employeeRepository.save(employee);

    }

    public List<EmployeeEntity> getPage(Integer pageNumber) {
        Sort sort = Sort.by(Sort.Direction.ASC, "fullName");

        PageRequest request = PageRequest.of(pageNumber, PAGE_LIMIT, sort);

        Page<EmployeeEntity> page = employeeRepository.findAll(request);

        if (!page.hasContent()) {
            return new ArrayList<>();
        }
        return page.getContent();
    }

    public List<EmployeeEntity> getPageByName(Integer pageNumber, String name) {
        PageRequest request = PageRequest.of(pageNumber, PAGE_LIMIT);

        Page<EmployeeEntity> page;
        page = employeeRepository.findByFullNameContainingOrderByFullName(name, request);

        if (page.isEmpty()) {
            return new ArrayList<>();
        }
        return page.getContent();
    }

    public EmployeeEntity getById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public EmployeeEntity getByLogin(String login) {
        return employeeRepository.findByLogin(login);
    }

    public EmployeeEntity update(EmployeeEntity employee) {

        EmployeeEntity updatedEmployee = getById(employee.getId());

        if (updatedEmployee == null) {
            throw new NotFoundException("Пользователь с id " + employee.getId() + " не найден!");
        }

        if (employee.getRole() != null) {
            updatedEmployee.setRole(employee.getRole());
        }
        if (employee.getFullName() != null) {
            updatedEmployee.setFullName(employee.getFullName());
        }
        if (employee.getPassword() != null) {
            updatedEmployee.setPassword(employee.getPassword());
        }
        if (employee.getEmail() != null) {
            updatedEmployee.setEmail(employee.getEmail());
        }
        if (employee.getPhoneNumber() != null) {
            updatedEmployee.setPhoneNumber(employee.getPhoneNumber());
        }
        if (employee.getPhoto() != null) {
            updatedEmployee.setPhoto(employee.getPhoto());
        }

        employeeRepository.save(updatedEmployee);

        return updatedEmployee;
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден!");
        }

        employeeRepository.deleteById(id);
    }

}
