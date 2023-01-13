package plugin.atb.booking.service;

import java.util.*;
import java.util.stream.*;

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
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final TeamService teamService;

    private final TeamMemberService teamMemberService;

    private final BookingService bookingService;

    @Transactional
    public void add(Employee employee) {

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

    public Page<Employee> getPage(Pageable pageable) {

        return employeeRepository.findAll(pageable);
    }

    public Page<Employee> getPageByName(String name, Pageable pageable) {

        if (name.isBlank()) {
            throw new IncorrectArgumentException(
                "Имя не может быть пустым или состоять только из пробелов");
        }

        return employeeRepository.findByFullNameContainingIgnoreCase(name, pageable);
    }

    public Employee getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return employeeRepository.findById(id).orElse(null);
    }

    public Employee getByLogin(String login) {

        if (login.isBlank()) {
            throw new IncorrectArgumentException(
                "Логин не может быть пустым или состоять только из пробелов");
        }

        return employeeRepository.findByLogin(login);
    }

    @Transactional
    public void update(Employee employee) {

        ValidationUtils.checkId(employee.getId());

        var employeeToUpdate = getById(employee.getId());

        if (employeeToUpdate == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + employee.getId());
        }

        if (employee.getRole() == null) {
            throw new IncorrectArgumentException("Роль сотрудника не указана");
        }

        validate(employee);

        employee
            .setLogin(employeeToUpdate.getLogin())
            .setPhoto(employeeToUpdate.getPhoto());

        employeeRepository.save(employee);

    }

    @Transactional
    public void delete(Long id) {

        var employee = getById(id);

        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + id);
        }

        var teamMembers = teamMemberService.getAllByEmployee(
            employee, Pageable.unpaged());

        if (!teamMembers.isEmpty()) {
            var teams = teamMembers.stream()
                .map(TeamMember::getTeam)
                .collect(Collectors.toSet());
            teams.removeIf(t -> !Objects.equals(t.getLeader().getId(), id));
            teamService.deleteAll(teams);
        }

        teamMemberService.deleteAllByEmployee(employee);
        bookingService.deleteAllByEmployee(employee);

        employeeRepository.delete(employee);
    }

    private void validate(Employee employee) {

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
