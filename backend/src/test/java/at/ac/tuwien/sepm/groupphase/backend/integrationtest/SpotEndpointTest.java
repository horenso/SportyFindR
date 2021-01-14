package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.SpotEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SpotEndpointTest implements TestData {
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SpotEndpoint spotEndpoint;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private SpotMapper spotMapper;

    @AfterEach
    public void afterEach() {
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    //positive tests
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createSpotWithNewLocation() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .build();
        LocationDto locationDto = LocationDto.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        SpotDto spotDto = SpotDto.builder()
            .name(NAME)
            .location(locationDto)
            .build();
        spotDto.setCategory(categoryMapper.categoryToCategoryDto(categoryRepository.save(categoryMapper.categoryDtoToCategory(categoryDto))));
        spotDto.setId(spotEndpoint.create(spotDto).getId());
        Optional<Spot> spot = spotRepository.findById(spotDto.getId());
        assertAll(
            () -> assertNotEquals(null, spot),
            () -> assertEquals(spotDto.getName(), spot.get().getName()),
            () -> assertEquals(spotDto.getDescription(), spot.get().getDescription()),
            () -> assertEquals(spotDto.getCategory(), categoryMapper.categoryToCategoryDto(spot.get().getCategory())),
            () -> assertEquals(spotDto.getLocation().getLongitude(), locationMapper.locationToLocationDto(spot.get().getLocation()).getLongitude()),
            () -> assertEquals(spotDto.getLocation().getLatitude(), locationMapper.locationToLocationDto(spot.get().getLocation()).getLatitude())
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createSpotOnExistingLocation() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .build();
        LocationDto locationDto = LocationDto.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationDto = locationMapper.locationToLocationDto(locationRepository.save(locationMapper.locationDtoToLocation(locationDto)));
        SpotDto spotDto = SpotDto.builder()
            .name(NAME)
            .location(locationDto)
            .build();
        spotDto.setCategory(categoryMapper.categoryToCategoryDto(categoryRepository.save(categoryMapper.categoryDtoToCategory(categoryDto))));
        spotDto.setId(spotEndpoint.create(spotDto).getId());
        Optional<Spot> spot = spotRepository.findById(spotDto.getId());
        assertAll(
            () -> assertNotEquals(null, spot),
            () -> assertEquals(spotDto.getName(), spot.get().getName()),
            () -> assertEquals(spotDto.getDescription(), spot.get().getDescription()),
            () -> assertEquals(spotDto.getCategory(), categoryMapper.categoryToCategoryDto(spot.get().getCategory())),
            () -> assertEquals(spotDto.getLocation().getLongitude(), locationMapper.locationToLocationDto(spot.get().getLocation()).getLongitude()),
            () -> assertEquals(spotDto.getLocation().getLatitude(), locationMapper.locationToLocationDto(spot.get().getLocation()).getLatitude())
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteSpot() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        List<Spot> spots1 = spotRepository.findAll();
        assertAll(
            () -> assertFalse(spots1.isEmpty())
        );
        spotEndpoint.delete(spot.getId());
        List<Spot> spots2 = spotRepository.findAll();
        assertAll(
            () -> assertTrue(spots2.isEmpty())
        );
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateSpot() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Category category2 = Category.builder()
            .name(CAT_NAME2)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        categoryRepository.save(category2);
        locationRepository.save(location);
        spotRepository.save(spot);
        SpotDto spot2 = SpotDto.builder()
            .id(spot.getId())
            .name(TEST_NEWS_SUMMARY)
            .description(TEST_NEWS_TEXT)
            .location(locationMapper.locationToLocationDto(location))
            .category(categoryMapper.categoryToCategoryDto(category2))
            .build();
        spotEndpoint.update(spot2);
        Optional<Spot> spot3 = spotRepository.findById(spot2.getId());
        assertAll(
            () -> assertEquals(spot2.getName(), spot3.get().getName()),
            () -> assertEquals(spot2.getDescription(), spot3.get().getDescription()),
            () -> assertEquals(spot2.getCategory(), categoryMapper.categoryToCategoryDto(spot3.get().getCategory())),
            () -> assertEquals(spot2.getLocation().getLongitude(), locationMapper.locationToLocationDto(spot3.get().getLocation()).getLongitude()),
            () -> assertEquals(spot2.getLocation().getLatitude(), locationMapper.locationToLocationDto(spot3.get().getLocation()).getLatitude())
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getSpotsByLocationTest() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Category category2 = Category.builder()
            .name(CAT_NAME2)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .location(location)
            .category(category)
            .build();

        Spot spot2 = Spot.builder()
            .id(spot.getId())
            .name(TEST_NEWS_SUMMARY)
            .description(TEST_NEWS_TEXT)
            .location(location)
            .category(category2)
            .build();
        categoryRepository.save(category);
        categoryRepository.save(category2);
        locationRepository.save(location);
        spotRepository.save(spot);
        spotRepository.save(spot2);
        List<SpotDto> spots = spotEndpoint.getSpotsByLocation(location.getId());
        assertAll(
            () -> assertEquals(spots.size(), 2),
            () -> assertEquals(spots.get(0).getId(), spot.getId()),
            () -> assertEquals(spots.get(0).getName(), spot.getName()),
            () -> assertEquals(spots.get(0).getDescription(), spot.getDescription()),
            () -> assertEquals(spots.get(0).getCategory(), categoryMapper.categoryToCategoryDto(spot.getCategory())),
            () -> assertEquals(spots.get(0).getLocation(), locationMapper.locationToLocationDto(spot.getLocation())),
            () -> assertEquals(spots.get(1).getId(), spot2.getId()),
            () -> assertEquals(spots.get(1).getName(), spot2.getName()),
            () -> assertEquals(spots.get(1).getDescription(), spot2.getDescription()),
            () -> assertEquals(spots.get(1).getCategory(), categoryMapper.categoryToCategoryDto(spot2.getCategory())),
            () -> assertEquals(spots.get(1).getLocation(), locationMapper.locationToLocationDto(spot2.getLocation()))
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getSpotsByIdTest() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        SpotDto spot2 = spotEndpoint.getOneById(spot.getId());
        assertAll(
            () -> assertEquals(spot2.getId(), spot.getId()),
            () -> assertEquals(spot2.getName(), spot.getName()),
            () -> assertEquals(spot2.getDescription(), spot.getDescription()),
            () -> assertEquals(spot2.getCategory(), categoryMapper.categoryToCategoryDto(spot.getCategory())),
            () -> assertEquals(spot2.getLocation(), locationMapper.locationToLocationDto(spot.getLocation()))
        );
    }

    //negative tests
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createSpotWithoutValidLocation() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .build();
        LocationDto locationDto = LocationDto.builder()
            .id(ID)
            .latitude(LAT)
            .longitude(LONG)
            .build();
        SpotDto spotDto = SpotDto.builder()
            .name(NAME)
            .location(locationDto)
            .build();
        spotDto.setCategory(categoryMapper.categoryToCategoryDto(categoryRepository.save(categoryMapper.categoryDtoToCategory(categoryDto))));
        Throwable e = assertThrows(ResponseStatusException.class, () -> spotEndpoint.create(spotDto));
        assertAll(
            () -> assertEquals(0, spotRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "400 BAD_REQUEST \"Location does not Exist\"")
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createSpotWithAnInvalidLocation() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .build();
        LocationDto locationDto = LocationDto.builder()
            .longitude(LONG)
            .build();
        SpotDto spotDto = SpotDto.builder()
            .name(NAME)
            .location(locationDto)
            .build();
        spotDto.setCategory(categoryMapper.categoryToCategoryDto(categoryRepository.save(categoryMapper.categoryDtoToCategory(categoryDto))));
        Throwable e = assertThrows(ResponseStatusException.class, () -> spotEndpoint.create(spotDto));
        assertAll(
            () -> assertEquals(0, spotRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "400 BAD_REQUEST \"Latitude must not be Null\"")
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createSpotWithId() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .build();
        LocationDto locationDto = LocationDto.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        SpotDto spotDto = SpotDto.builder()
            .id(ID)
            .name(NAME)
            .location(locationDto)
            .build();
        spotDto.setCategory(categoryMapper.categoryToCategoryDto(categoryRepository.save(categoryMapper.categoryDtoToCategory(categoryDto))));
        Throwable e = assertThrows(ResponseStatusException.class, () -> spotEndpoint.create(spotDto));
        assertAll(
            () -> assertEquals(0, spotRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "400 BAD_REQUEST \"Id must be null\"")
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createSpotWithoutACategory() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .build();
        LocationDto locationDto = LocationDto.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        SpotDto spotDto = SpotDto.builder()
            .category(categoryDto)
            .name(NAME)
            .location(locationDto)
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> spotEndpoint.create(spotDto));
        assertAll(
            () -> assertEquals(0, spotRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "400 BAD_REQUEST \"Spot must have a Category\"")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteSpotWithWrongId() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        List<Spot> spots1 = spotRepository.findAll();
        assertAll(
            () -> assertFalse(spots1.isEmpty())
        );
        Throwable e = assertThrows(ResponseStatusException.class, () -> spotEndpoint.delete(spot.getId() + 1));
        List<Spot> spots2 = spotRepository.findAll();
        assertAll(
            () -> assertFalse(spots2.isEmpty()),
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Spot does not exist\"")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateSpotWithWrongId() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Category category2 = Category.builder()
            .name(CAT_NAME2)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        categoryRepository.save(category2);
        locationRepository.save(location);
        spotRepository.save(spot);
        SpotDto spot2 = SpotDto.builder()
            .id(spot.getId()+1)
            .name(TEST_NEWS_SUMMARY)
            .description(TEST_NEWS_TEXT)
            .location(locationMapper.locationToLocationDto(location))
            .category(categoryMapper.categoryToCategoryDto(category2))
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> spotEndpoint.update(spot2));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Spot does not Exist\"")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getSpotsByLocationTestWithWrongLocation() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Category category2 = Category.builder()
            .name(CAT_NAME2)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .location(location)
            .category(category)
            .build();

        Spot spot2 = Spot.builder()
            .id(spot.getId())
            .name(TEST_NEWS_SUMMARY)
            .description(TEST_NEWS_TEXT)
            .location(location)
            .category(category2)
            .build();
        categoryRepository.save(category);
        categoryRepository.save(category2);
        locationRepository.save(location);
        spotRepository.save(spot);
        spotRepository.save(spot2);
        Long id = spot.getLocation().getId()+1;
        Throwable e = assertThrows(ResponseStatusException.class, () -> spotEndpoint.getSpotsByLocation(id));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Location with ID " + id + " cannot be found!\"")
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getSpotsByIdWithWrongId() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        Long id = spot.getId() + 1;
        Throwable e = assertThrows(ResponseStatusException.class, () -> spotEndpoint.getOneById(id));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Spot with ID " +id+" cannot be found!\"")
        );
    }
}
