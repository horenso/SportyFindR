package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class SpotMappingTest implements TestData {
    private final Spot spot = Spot.SpotBuilder.aSpot()
        .withId(ID)
        .withName(TEST_NEWS_TEXT)
        .build();
    @Autowired
    private SpotMapper spotMapper;
    @Test
    public void testtest() {
        SpotDto spotDto = spotMapper.spotToSpotDto(spot);
        assertAll(
            () -> assertEquals(ID, spotDto.getId()),
            () -> assertEquals(NAME, spotDto.getName()),
            () -> assertEquals(TEST_NEWS_TEXT, spotDto.getDescription()),
            () -> assertEquals(TEST_NEWS_SUMMARY, spotDto.getLocation()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, spotDto.getCategory())
        );
    }
}
