package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class EmployeeMapper {

    public Employee dtoToEmployee(EmployeeCreateDto dto, Role role) {
        return new Employee()
            .setRole(role)
            .setFullName(dto.getFullName())
            .setLogin(dto.getLogin())
            .setPassword(dto.getPassword())
            .setEmail(dto.getEmail())
            .setPhoneNumber(dto.getPhoneNumber());
    }

    public Employee dtoToEmployee(EmployeeUpdateDto dto, Role role) {
        return new Employee()
            .setId(dto.getId())
            .setRole(role)
            .setFullName(dto.getFullName())
            .setEmail(dto.getEmail())
            .setPassword(dto.getPassword())
            .setPhoneNumber(dto.getPhoneNumber());
    }

    public EmployeeGetDto employeeToDto(Employee employee) {
        var image = employee.getPhoto();
        Long imageId = null;
        if (image != null) {
            imageId = image.getId();
        }

        return new EmployeeGetDto(
            employee.getId(),
            employee.getRole().getName(),
            employee.getFullName(),
            employee.getLogin(),
            employee.getEmail(),
            employee.getPhoneNumber(),
            imageId);
    }

}
