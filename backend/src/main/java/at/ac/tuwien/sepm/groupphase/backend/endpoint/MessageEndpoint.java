package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping(value = "/api/v1/messages")
public class MessageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final SpotEndpoint spotEndpoint;

    @Autowired
    public MessageEndpoint(MessageService messageService,
                           MessageMapper messageMapper,
                           SpotEndpoint spotEndpoint) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.spotEndpoint = spotEndpoint;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get list of messages without details", authorizations = {@Authorization(value = "apiKey")})
    public List<MessageDto> findBySpot(
        @RequestParam(required = true, name = "spot") Long spotId) {
        LOGGER.info("GET /api/v1/messages?spot={}", spotId);
        List<Message> messages = this.messageService.findBySpot(spotId);
        return messageMapper.messageListToMessageDtoList(messages);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new message", authorizations = {@Authorization(value = "apiKey")})
    public MessageDto create(@Valid @RequestBody MessageDto messageDto) {
        LOGGER.info("POST /api/v1/messages body: {}", messageDto);
        MessageDto newMessage;
        newMessage = messageMapper.messageToMessageDto(
            messageService.create(messageMapper.messageDtoToMessage(messageDto)));
        spotEndpoint.dispatch(newMessage);
        return newMessage;
    }
}
