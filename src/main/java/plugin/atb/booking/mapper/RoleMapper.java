package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.model.*;

@Component
public class RoleMapper {

    public RoleDto roleToDto(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }

    public Role dtoToRole(RoleDto dto) {
        return new Role(dto.getId(), dto.getName());
    }

}
