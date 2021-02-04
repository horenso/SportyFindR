package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class SpotServiceTest implements TestData {
    @Autowired
    private SpotService spotService;
    @Autowired
    private SpotRepository spotRepository;

    @Test
    public void spotServiceCreate_withoutValidCategory_doesNotCreateSpot() {
        Category category = Category.builder()
            .id(1L)
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

        Throwable e = assertThrows(ValidationException.class, () -> spotService.create(spot));
        assertAll(
            () -> assertEquals(0, spotRepository.findAll().size()),
            () -> assertEquals(e.getMessage(),"Category does not Exist")
        );

    }
    @AfterEach
    public void afterEach(){
        spotRepository.deleteAll();
    }
}
