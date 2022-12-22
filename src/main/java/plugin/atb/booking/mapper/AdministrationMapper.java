package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class AdministrationMapper {

    public AdministrationCreateDto adminToCreateDto(AdministratingEntity admin) {
        var dto = new AdministrationCreateDto(
            admin.getEmployee().getId(),
            admin.getOffice().getId());
        return dto;
    }

    public AdministratingEntity createDtoToAdmin(
        EmployeeEntity employee, OfficeEntity office
    ) {
        var admin = new AdministratingEntity()
            .setEmployee(employee)
            .setOffice(office);
        return admin;
    }

    public AdministrationDto adminToDto(AdministratingEntity administrating) {
        var dto = new AdministrationDto(
            administrating.getId(),
            administrating.getEmployee().getId(),
            administrating.getOffice().getId());
        return dto;
    }

    public AdministratingEntity dtoToAdmin(
        AdministrationDto dto, EmployeeEntity employee, OfficeEntity office
    ) {
        var administration = new AdministratingEntity()
            .setId(dto.getId())
            .setEmployee(employee)
            .setOffice(office);
        return administration;
    }

}
