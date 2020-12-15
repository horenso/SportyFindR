package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto location);

    LocationDto locationToLocationDto(Location location);

    List<LocationDto> entityToListDto(List<Location> locations);
}
