package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.LocationEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LocationEndpointTest implements TestData {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private LocationEndpoint locationEndpoint;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private UserRepository userRepository;
    @AfterEach
    public void afterEach() {
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }
    //positive Test
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllLocations() {
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Location location2 = Location.builder()
            .latitude(LAT2)
            .longitude(LONG2)
            .build();
        locationRepository.save(location2);
        List<LocationDto> locationDtos = locationEndpoint.find(null, null, null, null);
        assertAll(
            () -> assertEquals(locationDtos.size(), 2),
            () -> assertEquals(locationDtos.get(0).getId(), location.getId()),
            () -> assertEquals(locationDtos.get(0).getLatitude(), location.getLatitude()),
            () -> assertEquals(locationDtos.get(0).getLongitude(), location.getLongitude()),
            () -> assertEquals(locationDtos.get(1).getId(), location2.getId()),
            () -> assertEquals(locationDtos.get(1).getLatitude(), location2.getLatitude()),
            () -> assertEquals(locationDtos.get(1).getLongitude(), location2.getLongitude())
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getOneLocation() {
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Location location2 = Location.builder()
            .latitude(LAT2)
            .longitude(LONG2)
            .build();
        locationRepository.save(location2);
        locationEndpoint.find(null, null, null, null);
        LocationDto locationDto = locationEndpoint.getOneById(location.getId());
        assertAll(
            () -> assertEquals(locationDto.getId(), location.getId()),
            () -> assertEquals(locationDto.getLatitude(), location.getLatitude()),
            () -> assertEquals(locationDto.getLongitude(), location.getLongitude())
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllLocationsWhenThereAreNoLocations() {
        assertAll(
            () -> assertTrue(locationEndpoint.find(null, null, null, null).isEmpty())
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void filterAllLocationsWithCategory() {
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
        Location location2 = Location.builder()
            .latitude(LAT2)
            .longitude(LONG2)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .name(NAME)
            .owner(user)
            .location(location)
            .category(category)
            .build();
        Spot spot2 = Spot.builder()
            .name(TEST_NEWS_SUMMARY)
            .description(TEST_NEWS_TEXT)
            .owner(user)
            .location(location2)
            .category(category2)
            .build();
        categoryRepository.save(category);
        categoryRepository.save(category2);
        locationRepository.save(location);
        locationRepository.save(location2);
        spotRepository.save(spot);
        spotRepository.save(spot2);
        List<LocationDto> locationDtos = locationEndpoint.find(category.getId(),null,null,null);
        assertAll(
            () -> assertEquals(locationDtos.size(), 1),
            () -> assertEquals(locationDtos.get(0).getId(), location.getId()),
            () -> assertEquals(locationDtos.get(0).getLatitude(), location.getLatitude()),
            () -> assertEquals(locationDtos.get(0).getLongitude(), location.getLongitude())
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void filterAllLocationsWithLatLongRadius() {
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
        Location location2 = Location.builder()
            .latitude(LAT2)
            .longitude(LONG2)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        Spot spot2 = Spot.builder()
            .name(TEST_NEWS_SUMMARY)
            .owner(user)
            .description(TEST_NEWS_TEXT)
            .location(location2)
            .category(category2)
            .build();
        categoryRepository.save(category);
        categoryRepository.save(category2);
        locationRepository.save(location);
        locationRepository.save(location2);
        spotRepository.save(spot);
        spotRepository.save(spot2);
        List<LocationDto> locationDtos = locationEndpoint.find(null,LAT,LONG,RADIUS);
        assertAll(
            () -> assertEquals(locationDtos.size(), 1),
            () -> assertEquals(locationDtos.get(0).getId(), location.getId()),
            () -> assertEquals(locationDtos.get(0).getLatitude(), location.getLatitude()),
            () -> assertEquals(locationDtos.get(0).getLongitude(), location.getLongitude())
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void filterAllLocationsWithCategoryLatLongRadius() {
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
        Location location2 = Location.builder()
            .latitude(LAT2)
            .longitude(LONG2)
            .build();
        Location location3 = Location.builder()
            .latitude(LAT3)
            .longitude(LONG3)
            .build();
        Location location4 = Location.builder()
            .latitude(LAT4)
            .longitude(LONG4)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        Spot spot2 = Spot.builder()
            .owner(user)
            .name(TEST_NEWS_SUMMARY)
            .description(TEST_NEWS_TEXT)
            .location(location2)
            .category(category2)
            .build();
        Spot spot3 = Spot.builder()
            .owner(user)
            .name(SPOT_NAME)
            .location(location3)
            .category(category)
            .build();
        Spot spot4 = Spot.builder()
            .owner(user)
            .name(SPOT_NAME)
            .location(location4)
            .category(category2)
            .build();
        categoryRepository.save(category);
        categoryRepository.save(category2);
        locationRepository.save(location);
        locationRepository.save(location2);
        locationRepository.save(location3);
        locationRepository.save(location4);
        spotRepository.save(spot);
        spotRepository.save(spot2);
        spotRepository.save(spot3);
        spotRepository.save(spot4);
        List<LocationDto> locationDtos = locationEndpoint.find(category2.getId(), LAT,LONG,RADIUS);
        assertAll(
            () -> assertEquals(locationDtos.size(), 1),
            () -> assertEquals(locationDtos.get(0).getId(), location4.getId()),
            () -> assertEquals(locationDtos.get(0).getLatitude(), location4.getLatitude()),
            () -> assertEquals(locationDtos.get(0).getLongitude(), location4.getLongitude())
        );
    }
    /* negative Test

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getOneLocationWithWrongId() {
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Long id = location.getId()+1;
        Throwable e = assertThrows(ResponseStatusException.class, () -> locationEndpoint.getOneById(id));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Location with ID " + id + " cannot be found!\"")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void filterAllLocationsWithNoMatchingLocations() {
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
        Location location2 = Location.builder()
            .latitude(LAT2)
            .longitude(LONG2)
            .build();
        Location location3 = Location.builder()
            .latitude(LAT3)
            .longitude(LONG3)
            .build();
        Location location4 = Location.builder()
            .latitude(LAT4)
            .longitude(LONG4)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        Spot spot2 = Spot.builder()
            .owner(user)
            .name(TEST_NEWS_SUMMARY)
            .description(TEST_NEWS_TEXT)
            .location(location2)
            .category(category2)
            .build();
        Spot spot3 = Spot.builder()
            .owner(user)
            .name(SPOT_NAME)
            .location(location3)
            .category(category)
            .build();
        Spot spot4 = Spot.builder()
            .owner(user)
            .name(SPOT_NAME)
            .location(location4)
            .category(category)
            .build();
        categoryRepository.save(category);
        categoryRepository.save(category2);
        locationRepository.save(location);
        locationRepository.save(location2);
        locationRepository.save(location3);
        locationRepository.save(location4);
        spotRepository.save(spot);
        spotRepository.save(spot2);
        spotRepository.save(spot3);
        spotRepository.save(spot4);
        Throwable e = assertThrows(ResponseStatusException.class, () -> locationEndpoint.find(category2.getId(), LAT,LONG,RADIUS));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"No Location within " + RADIUS + "km found.\"")
        );
    }
     */
}
