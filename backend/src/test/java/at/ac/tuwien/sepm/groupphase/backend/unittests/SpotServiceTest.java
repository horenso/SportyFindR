package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.lang.model.element.Name;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        boolean b = false;
        Category category = Category.builder()
            .id(ID)
            .build();
        Location location = Location.builder()
            .latitude(10.0)
            .longitude(10.0)
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
}
