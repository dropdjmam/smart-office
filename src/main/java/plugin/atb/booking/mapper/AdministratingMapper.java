package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class AdministratingMapper {

    public Administrating dtoToAdministrating(Employee employee, Office office) {
        return new Administrating()
            .setEmployee(employee)
            .setOffice(office);
    }

    public AdministratingDto administratingToDto(Administrating administrating) {
        return new AdministratingDto(
            administrating.getId(),
            administrating.getEmployee().getId(),
            administrating.getOffice().getId());
    }

}
