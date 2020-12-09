package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Mapping(source = "spotId", target = "spot.id")
    Message messageDtoToMessage(MessageDto message);

    @Mapping(source = "spot.id", target = "spotId")
    MessageDto messageToMessageDto(Message message);

    List<Message> messageDtoListToMessageList(List<MessageDto> messageDtoList);
    List<MessageDto> messageListToMessageDtoList(List<Message> messageList);
}

