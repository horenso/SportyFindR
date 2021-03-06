package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {
    @Mapping(source = "roleIds", target = "roles")
    ApplicationUser userDtoToApplicationUser(UserDto userDto);

    @Mapping(source = "roles", target = "roleIds")
    @Mapping(target = "password", expression = "java(null)")
    UserDto applicationUserToUserDto(ApplicationUser user);

    List<UserDto> applicationUserListToUserDtoList(List<ApplicationUser> users);

    List<UserDto> applicationUserListToUserDtoList(Set<ApplicationUser> users);

    default List<Long> rolesToRoleIds(Set<Role> roles) {
        if (roles != null) {
            List<Long> roleIds = new ArrayList<>();
            for (Role role : roles) {
                if (role != null) {
                    roleIds.add(role.getId());
                }
            }
            return roleIds;
        } else {
            return new ArrayList<>();
        }
    }

    default Set<Role> roleIdsToRoles(List<Long> roleIds) {
        if (roleIds != null) {
            Set<Role> roles = new HashSet<>();
            for (Long id : roleIds) {
                if (id != null) {
                    roles.add(new Role().builder().id(id).build());
                }
            }
            return roles;
        } else {
            return new HashSet<>();
        }
    }

}
