package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper {

    Message messageDtoToMessage(MessageDto message);
    MessageDto messageToMessageDto(Message message);
}

