package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/messages")
@Slf4j
public class MessageEndpoint {

    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final SpotEndpoint spotEndpoint;
    private final SpotService spotService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get list of messages without details", authorizations = {@Authorization(value = "apiKey")})
    public List<MessageDto> findBySpot(
        @RequestParam(name = "spot") Long spotId) {
        log.info("GET /api/v1/messages?spot={}", spotId);
        List<Message> messages = this.messageService.findBySpot(spotId);
        return messageMapper.messageListToMessageDtoList(messages);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new message", authorizations = {@Authorization(value = "apiKey")})
    public MessageDto create(@Valid @RequestBody MessageDto messageDto) {
        log.info("POST /api/v1/messages body: {}", messageDto);
        MessageDto newMessage;
        newMessage = messageMapper.messageToMessageDto(
            messageService.create(messageMapper.messageDtoToMessage(messageDto)));
        return newMessage;
    }
}
