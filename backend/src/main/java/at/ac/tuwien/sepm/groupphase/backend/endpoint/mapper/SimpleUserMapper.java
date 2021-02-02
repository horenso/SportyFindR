package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleUserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper
public interface SimpleUserMapper {

    ApplicationUser simpleUserDtoToUser(SimpleUserDto simpleUserDto);

    SimpleUserDto userToSimpleUserDto(ApplicationUser user);

    List<SimpleUserDto> entityToListDto(List<ApplicationUser> hashtags);
}
