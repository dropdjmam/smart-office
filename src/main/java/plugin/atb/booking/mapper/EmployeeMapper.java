package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class EmployeeMapper {

    public EmployeeEntity dtoToEmployee(EmployeeCreateDto dto, RoleEntity role) {
        EmployeeEntity employee = new EmployeeEntity()
            .setRole(role)
            .setFullName(dto.getFullName())
            .setLogin(dto.getLogin())
            .setPassword(dto.getPassword())
            .setEmail(dto.getEmail())
            .setPhoneNumber(dto.getPhoneNumber())
            .setPhoto(dto.getPhoto());

        return employee;
    }

    public EmployeeEntity dtoToEmployee(EmployeeUpdateDto dto, RoleEntity role) {

        EmployeeEntity employee = new EmployeeEntity()
            .setId(dto.getId())
            .setRole(role)
            .setFullName(dto.getFullName())
            .setEmail(dto.getEmail())
            .setPassword(dto.getPassword())
            .setPhoneNumber(dto.getPhoneNumber())
            .setPhoto(dto.getPhoto());

        return employee;
    }

    public EmployeeGetDto employeeToDto(EmployeeEntity employee) {

        EmployeeGetDto employeeGetDto = new EmployeeGetDto(
            employee.getId(),
            employee.getRole().getName(),
            employee.getFullName(),
            employee.getLogin(),
            employee.getEmail(),
            employee.getPhoneNumber(),
            employee.getPhoto()
        );

        return employeeGetDto;

    }

}
