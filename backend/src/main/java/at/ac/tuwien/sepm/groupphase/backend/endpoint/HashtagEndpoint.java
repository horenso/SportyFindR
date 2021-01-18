package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.HashtagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HashtagMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.HashtagService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/hashtags")
@RequiredArgsConstructor
public class HashtagEndpoint {

    private final HashtagService hashtagService;
    private final HashtagMapper hashtagMapper;

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{name}")
    @ApiOperation(value = "Get a hashtag by name", authorizations = {@Authorization(value = "apiKey")})
    public HashtagDto getById(@PathVariable("name") String name) {
        log.info("GET /api/v1/hashtags/{}", name);
        return hashtagMapper.hashtagToHashtagDto(hashtagService.getByName(name));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @ApiOperation(value = "Get all hashtags", authorizations = {@Authorization(value = "apiKey")})
    public List<HashtagDto> getAll() {
        log.info("GET /api/v1/hashtags");
        return hashtagMapper.entityToListDto((hashtagService.findAll()));
    }
}
