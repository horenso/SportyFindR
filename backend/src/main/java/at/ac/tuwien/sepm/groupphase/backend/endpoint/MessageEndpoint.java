package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/messages")
public class MessageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageEndpoint(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get list of messages without details", authorizations = {@Authorization(value = "apiKey")})
    public List<MessageDto> findBySpot(
        @RequestParam(required = true, name = "spot") Long spotId) {
        LOGGER.info("GET /api/v1/messages?spot={}", spotId);
        List<Message> messages = this.messageService.findBySpot(spotId);
        List<MessageDto> messageDtoList = new LinkedList<>();

        messages.forEach(messageDto -> { messageDtoList.add(messageMapper.messageToMessageDto(messageDto)); });
        return messageDtoList;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new message", authorizations = {@Authorization(value = "apiKey")})
    public MessageDto create(@Valid @RequestBody MessageDto messageDto) {
        LOGGER.info("POST /api/v1/messages body: {}", messageDto);
        return messageMapper.messageToMessageDto(messageService.create(messageMapper.messageDtoToMessage(messageDto)));
    }

//    @GetMapping(value = "/{id}")
//    @ApiOperation(value = "Get detailed information about a specific message",
//        authorizations = {@Authorization(value = "apiKey")})
//    public DetailedMessageDto find(@PathVariable Long id) {
//        return null;
////        LOGGER.info("GET /api/v1/messages/{}", id);
////        return messageMapper.messageToDetailedMessageDto(messageService.findOne(id));
//    }
//
//    @Secured("ROLE_ADMIN")
//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping
//    @ApiOperation(value = "Publish a new message", authorizations = {@Authorization(value = "apiKey")})
//    public DetailedMessageDto create(@Valid @RequestBody MessageInquiryDto messageDto) {
//        return null;
////        LOGGER.info("POST /api/v1/messages body: {}", messageDto);
////        return messageMapper.messageToDetailedMessageDto(
////            messageService.publishMessage(messageMapper.messageInquiryDtoToMessage(messageDto)));
//    }
}
