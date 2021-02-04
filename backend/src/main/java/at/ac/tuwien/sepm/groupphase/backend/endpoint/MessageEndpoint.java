package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageSearchObject;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/messages")
@Slf4j
public class MessageEndpoint {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get page of messages without details by spot",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<MessageDto> findBySpot(
        @RequestParam Long spotId,
        @RequestParam(defaultValue = "0", required = false) int page,
        @RequestParam(defaultValue = "5", required = false) int size) {

        log.info("GET /api/v1/messages?spotId={} page: (size: {}, page: {})", spotId, size, page);

        Pageable pageable = PageRequest.of(page, size);

        try {
            return messageMapper.messagePageToMessageDtoPage(messageService.findBySpotPaged(spotId, pageable));
        } catch (NotFoundException2 e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create one new message", authorizations = {@Authorization(value = "apiKey")})
    public MessageDto create(@Valid @RequestBody MessageDto messageDto) {
        log.info("POST /api/v1/messages body: {}", messageDto);
        MessageDto newMessage;
        try {
            newMessage = messageMapper.messageToMessageDto(
                messageService.create(messageMapper.messageDtoToMessage(messageDto)));
            return newMessage;
        } catch (NotFoundException2 e) {
            log.error(HttpStatus.NOT_FOUND + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ValidationException e) {
            log.error(HttpStatus.UNPROCESSABLE_ENTITY + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get one message by id", authorizations = {@Authorization(value = "apiKey")})
    public MessageDto getById(@PathVariable("id") Long id) {
        log.info("GET /api/v1/messages/{}", id);
        try {
            return messageMapper.messageToMessageDto(messageService.getById(id));
        } catch (NotFoundException2 e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete one message by id", authorizations = {@Authorization(value = "apiKey")})
    public void deleteById(@PathVariable("id") Long id) {
        log.info("DELETE /api/v1/messages/{}", id);
        try {
            messageService.deleteById(id);
        } catch (NotFoundException2 | ServiceException e) {
            log.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (WrongUserException e) {
            log.error(HttpStatus.FORBIDDEN + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Filter messages by hashtag, username, time and category",
        authorizations = {@Authorization(value = "apiKey")})
    public Page<MessageDto> filter(
        @PageableDefault(size = 20)
        @SortDefault.SortDefaults({
            @SortDefault(sort = "id", direction = Sort.Direction.ASC)})
            Pageable pageable,
        @RequestParam(required = false) Long categoryMes,
        @RequestParam(required = false) String hashtag,
        @RequestParam(required = false, defaultValue = "0") String user,
        @RequestParam(required = false, defaultValue = "1000-01-01")
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate time) {

        log.info("GET /api/v1/messages/filter?categoryMes={}&hashtag={}&user={}&time={}",
            categoryMes, hashtag, user, time);

        MessageSearchObject messageSearchObject = new MessageSearchObject(categoryMes, hashtag, user, time.atStartOfDay());

        try {
            return messageMapper.messagePageToMessageDtoPage(messageService.filter(messageSearchObject, pageable));
        } catch (ServiceException e) {
            log.error(HttpStatus.UNPROCESSABLE_ENTITY + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

}
