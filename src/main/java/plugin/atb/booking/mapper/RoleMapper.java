package plugin.atb.booking.mapper;

import org.springframework.stereotype.*;
import plugin.atb.booking.dto.*;
import plugin.atb.booking.entity.*;

@Component
public class RoleMapper {

    public RoleDto roleToDto(RoleEntity role) {
        return new RoleDto(role.getId(), role.getName());
    }

    public RoleEntity dtoToRole(RoleDto dto) {
        return new RoleEntity(dto.getId(), dto.getName());
    }

}
