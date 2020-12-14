package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SpotMapper {

    @Mapping(target = "location")
    Spot spotDtoToSpot(SpotDto spot);

    SpotDto spotToSpotDto(Spot spot);
}
