package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MessageMapper {

    @Mapping(target = "spot", source = "spotId")
    Message messageDtoToMessage(MessageDto message);

    @Mapping(target = "spotId", source = "spot.id")
    MessageDto messageToMessageDto(Message message);
}

