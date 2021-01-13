package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RoleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    Role roleDtoToRole(RoleDto roleDto);

    RoleDto roleToRoleDto(Role Role);

    List<RoleDto> roleListToRoleDtoList(List<Role> roles);
}
