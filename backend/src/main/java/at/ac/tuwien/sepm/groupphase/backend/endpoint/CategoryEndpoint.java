package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/categories")
public class CategoryEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryEndpoint(CategoryService categoryService,
                            CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new category", authorizations = {@Authorization(value = "apiKey")})
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        LOGGER.info("POST /api/v1/categories body: {}", categoryDto);
        try {
            return categoryMapper.categoryToCategoryDto(
                categoryService.create(categoryMapper.categoryDtoToCategory(categoryDto)));
        } catch (ServiceException | ValidationException e) {
            LOGGER.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete category", authorizations = {@Authorization(value = "apiKey")})
    public void delete(@PathVariable("id") Long id) {
        LOGGER.info("DELETE /api/v1/categories id: {}", id);
        try {
            categoryService.deleteById(id);
        } catch (NotFoundException2 e) {
            LOGGER.error(HttpStatus.NOT_FOUND + " " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/all")
    @ApiOperation(value = "Get all categories", authorizations = {@Authorization(value = "apiKey")})
    public List<CategoryDto> getAll() {
        LOGGER.info("GET /api/v1/categories/all");
        return categoryMapper.entityToListDto((categoryService.findAll()));
    }
}
