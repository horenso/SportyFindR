package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReactionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ReactionMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/reactions")
public class ReactionEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReactionService reactionService;
    private final ReactionMapper reactionMapper;

    @Autowired
    public ReactionEndpoint(ReactionService reactionService, ReactionMapper reactionMapper) {
        this.reactionService = reactionService;
        this.reactionMapper = reactionMapper;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new Reaction", authorizations = {@Authorization(value = "apiKey")})
    public ReactionDto create(@Valid @RequestBody ReactionDto reactionDto) {
        LOGGER.info("POST /api/v1/reactions body: {}", reactionDto);
        return reactionMapper.reactionToReactionDto(
            reactionService.create(reactionMapper.reactionDtoToReaction(reactionDto)));
    }
}
