package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class EmployeeMapper {

    public EmployeeEntity dtoToEmployee(EmployeeCreateDto dto, RoleEntity role) {
        var employee = new EmployeeEntity()
            .setRole(role)
            .setFullName(dto.getFullName())
            .setLogin(dto.getLogin())
            .setPassword(dto.getPassword())
            .setEmail(dto.getEmail())
            .setPhoneNumber(dto.getPhoneNumber());

        return employee;
    }

    public EmployeeEntity dtoToEmployee(EmployeeUpdateDto dto, RoleEntity role) {

        var employee = new EmployeeEntity()
            .setId(dto.getId())
            .setRole(role)
            .setFullName(dto.getFullName())
            .setEmail(dto.getEmail())
            .setPassword(dto.getPassword())
            .setPhoneNumber(dto.getPhoneNumber());

        return employee;
    }

    public EmployeeGetDto employeeToDto(EmployeeEntity employee) {
        var image = employee.getPhoto();
        Long imageId = null;
        if (image != null) {
            imageId = image.getId();
        }

        var employeeGetDto = new EmployeeGetDto(
            employee.getId(),
            employee.getRole().getName(),
            employee.getFullName(),
            employee.getLogin(),
            employee.getEmail(),
            employee.getPhoneNumber(),
            imageId
        );

        return employeeGetDto;

    }

}
