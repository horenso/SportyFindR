package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReactionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ReactionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/reactions")
@RequiredArgsConstructor
public class ReactionEndpoint {

    private final ReactionService reactionService;
    private final ReactionMapper reactionMapper;

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new Reaction", authorizations = {@Authorization(value = "apiKey")})
    public ReactionDto create(@Valid @RequestBody ReactionDto reactionDto) {
        log.info("POST /api/v1/reactions body: {}", reactionDto);
        return reactionMapper.reactionToReactionDto(
            reactionService.create(reactionMapper.reactionDtoToReaction(reactionDto)));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get list of Reactions", authorizations = {@Authorization(value = "apiKey")})
    public List<ReactionDto> getReactionsByMessage(@RequestParam(name = "message") Long messageId) {
        log.info("GET /api/v1/reactions?message={}", messageId);
        List<Reaction> reactions = reactionService.getReactionsByMessageId(messageId);
        List<ReactionDto> reactionDtos = new ArrayList<>();

        reactions.forEach(reaction -> reactionDtos.add(reactionMapper.reactionToReactionDto(reaction)));
        return reactionDtos;
    }
}
