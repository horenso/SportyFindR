package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.HashtagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.HashtagMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.HashtagService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/hashtags")
@CrossOrigin
@RequiredArgsConstructor
public class HashtagEndpoint {

    private final HashtagService hashtagService;
    private final HashtagMapper hashtagMapper;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{name}")
    @CrossOrigin
    @ApiOperation(value = "Get a hashtag by name", authorizations = {@Authorization(value = "apiKey")})
    public HashtagDto getByName(@PathVariable("name") String name) {
        log.info("GET /api/v1/hashtags/{}", name);
        try {
            return hashtagMapper.hashtagToHashtagDto(hashtagService.getByName(name));
        } catch (ValidationException e) {
            log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/filter")
    @ApiOperation(value = "Get hashtag list by name", authorizations = {@Authorization(value = "apiKey")})
    public List<HashtagDto> searchByPartialName(@RequestParam(required = false) String name) {
        log.info("GET /api/v1/hashtags/filter?name={}", name);

        List<HashtagDto> h = hashtagMapper.entityToListDto(hashtagService.searchByName(name));
        h.forEach(a -> {
            log.info(a.toString());
        });
        return h;
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @CrossOrigin
    @ApiOperation(value = "Get all hashtags", authorizations = {@Authorization(value = "apiKey")})
    public List<HashtagDto> getAll() {
        log.info("GET /api/v1/hashtags");
        List<HashtagDto> hashtagDtoList = hashtagMapper.entityToListDto((hashtagService.findAll()));
        for (HashtagDto hashtagDto : hashtagDtoList) {
            log.info(hashtagDto.toString());
        }
        return hashtagDtoList;
    }
}
