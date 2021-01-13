package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    ApplicationUser userDtoToApplicationUser(UserDto userDto);

    UserDto applicationUserToUserDto(ApplicationUser user);

    List<UserDto> applicationUserListToUserDtoList(List<ApplicationUser> users);
}
