package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Mapping(source = "spotId", target = "spot.id")
    Message messageDtoToMessage(MessageDto message);

    @Mapping(source = "spot.id", target = "spotId")
    MessageDto messageToMessageDto(Message message);

    List<MessageDto> entityToListDto(List<Message> messages);

    List<Message> messageDtoListToMessageList(List<MessageDto> messageDtoList);

    List<MessageDto> messageListToMessageDtoList(List<Message> messageList);

    default Page<MessageDto> messagePageToMessageDtoPage(Page<Message> messagePage) {
        return messagePage.map(this::messageToMessageDto);
    }
}

