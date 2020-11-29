package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundInDatabaseException;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/spots")
public class SpotEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SpotService spotService;
    private final SpotMapper spotMapper;

    @Autowired
    public SpotEndpoint(SpotService spotService, SpotMapper spotMapper) {
        this.spotService = spotService;
        this.spotMapper = spotMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new spot", authorizations = {@Authorization(value = "apiKey")})
    public SpotDto create(@Valid @RequestBody SpotDto spotDto) {
        LOGGER.info("POST /api/v1/messages body: {}", spotDto);
        return spotMapper.spotToSpotDto(
            spotService.create(spotMapper.spotDtoToSpot(spotDto)));
    }



        @Secured("ROLE_ADMIN")
        @ResponseStatus(HttpStatus.OK)
        @DeleteMapping(value = "/{id}")
        @ApiOperation(value = "Create a new spot", authorizations = {@Authorization(value = "apiKey")})
        public void delete(@PathVariable("id") Long id) {
        LOGGER.info("DELETE /api/v1/spots id: {}", id);
        try {
            spotService.deleteById(id);
        }catch (NotFoundInDatabaseException e){
            LOGGER.error(HttpStatus.NOT_FOUND +" "+e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
