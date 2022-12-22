package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public void add(EmployeeEntity employee) {

        validate(employee);

        if (employee.getLogin() == null) {
            throw new IncorrectArgumentException("Логин сотрудника не указан");
        }

        boolean exists = employeeRepository.existsEmployeeByLoginOrEmail(
            employee.getLogin(),
            employee.getEmail()
        );

        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Пользователь с таким логином или почтой уже существует: %s, %s",
                employee.getLogin(), employee.getEmail()));
        }

        employeeRepository.save(employee);

    }

    public Page<EmployeeEntity> getPage(Pageable pageable) {

        return employeeRepository.findAll(pageable);
    }

    public Page<EmployeeEntity> getPageByName(String name, Pageable pageable) {

        if (name.isBlank()) {
            throw new IncorrectArgumentException(
                "Имя не может быть пустым или состоять только из пробелов");
        }

        return employeeRepository.findByFullNameContaining(name, pageable);
    }

    public EmployeeEntity getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return employeeRepository.findById(id).orElse(null);
    }

    public EmployeeEntity getByLogin(String login) {

        if (login.isBlank()) {
            throw new IncorrectArgumentException(
                "Логин не может быть пустым или состоять только из пробелов");
        }

        return employeeRepository.findByLogin(login);
    }

    public void update(EmployeeEntity employee) {

        ValidationUtils.checkId(employee.getId());

        if (getById(employee.getId()) == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + employee.getId());
        }

        if (employee.getRole() == null) {
            throw new IncorrectArgumentException("Роль сотрудника не указана");
        }

        validate(employee);

        employeeRepository.save(employee);

    }

    public void delete(Long id) {

        ValidationUtils.checkId(id);

        if (getById(id) == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + id);
        }

        employeeRepository.deleteById(id);
    }

    private void validate(EmployeeEntity employee) {

        if (employee.getFullName() == null) {
            throw new IncorrectArgumentException("Полное имя сотрудника не указано");
        }
        if (employee.getPassword() == null) {
            throw new IncorrectArgumentException("Пароль сотрудника не указан");
        }
        if (employee.getEmail() == null) {
            throw new IncorrectArgumentException("Почта сотрудника не указана");
        }
        if (employee.getPhoneNumber() == null) {
            throw new IncorrectArgumentException("Телефон сотрудника не указан");
        }

    }

}
