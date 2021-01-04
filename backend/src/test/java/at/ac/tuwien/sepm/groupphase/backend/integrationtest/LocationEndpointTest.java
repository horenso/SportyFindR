package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.LocationEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.SpotEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

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
    @AfterEach
    public void afterEach() {
        locationRepository.deleteAll();
    }
    //positive Test
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllLocations() {
        Location location = Location.builder()
            .latitude(10.0)
            .longitude(10.0)
            .build();
        locationRepository.save(location);
        Location location2 = Location.builder()
            .latitude(-110.0)
            .longitude(-110.0)
            .build();
        locationRepository.save(location2);
        List<LocationDto> locationDtos = locationEndpoint.findAll();
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
            .latitude(10.0)
            .longitude(10.0)
            .build();
        locationRepository.save(location);
        Location location2 = Location.builder()
            .latitude(-110.0)
            .longitude(-110.0)
            .build();
        locationRepository.save(location2);
        locationEndpoint.findAll();
        LocationDto locationDto = locationEndpoint.getOneById(location.getId());
        assertAll(
            () -> assertEquals(locationDto.getId(), location.getId()),
            () -> assertEquals(locationDto.getLatitude(), location.getLatitude()),
            () -> assertEquals(locationDto.getLongitude(), location.getLongitude())
        );
    }
    //negative Test
    //TODO: Fix ErrorHandling
    /*
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllLocationsWhenThereAreNoLocations() {
        Throwable e = assertThrows(ResponseStatusException.class, () -> locationEndpoint.findAll());
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"\"No locations could be found.\"\"")
        );
    }
    */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getOneLocationWithWrongId() {
        Location location = Location.builder()
            .latitude(10.0)
            .longitude(10.0)
            .build();
        locationRepository.save(location);
        Long id = location.getId()+1;
        Throwable e = assertThrows(ResponseStatusException.class, () -> locationEndpoint.getOneById(id));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Location with ID " + id + " cannot be found!\"")
        );
    }
}
