package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RoleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface RoleMapper {
    @Mapping(source = "userIds", target = "applicationUsers")
    Role roleDtoToRole(RoleDto roleDto);

    @Mapping(source = "applicationUsers", target = "userIds")
    RoleDto roleToRoleDto(Role Role);

    List<RoleDto> roleListToRoleDtoList(List<Role> roles);

    List<RoleDto> roleListToRoleDtoList(Set<Role> roles);

    default List<Long> applicationUsersToUserIds(Set<ApplicationUser> users) {
        if (users != null) {
            List<Long> userIds = new ArrayList<>();
            for (ApplicationUser user : users) {
                if (user != null) {
                    userIds.add(user.getId());
                }
            }
            return userIds;
        } else {
            return new ArrayList<>();
        }
    }

    default Set<ApplicationUser> userIdsToApplicationUsers(List<Long> userIds) {
        if (userIds != null) {
            Set<ApplicationUser> users = new HashSet<>();
            for (Long id : userIds) {
                if (id != null) {
                    users.add(new ApplicationUser().builder().id(id).build());
                }
            }
            return users;
        } else {
            return new HashSet<>();
        }
    }
}
