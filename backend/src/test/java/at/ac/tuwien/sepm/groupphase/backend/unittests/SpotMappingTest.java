package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
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
    private final Spot spot = Spot.builder()
        .name(NAME)
        .description(DESCRIPTION)
        .location(LOCATION)
        .category(CATEGORY)
        .build();

    @Autowired
    private SpotMapper spotMapper;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    public void spotToSpotDtoMapperTest() {
        SpotDto spotDto = spotMapper.spotToSpotDto(spot);
        LocationDto locationDto = locationMapper.locationToLocationDto(spot.getLocation());
        CategoryDto categoryDto = categoryMapper.categoryToCategoryDto(spot.getCategory());
        assertAll(
            () -> assertEquals(null, spotDto.getId()),
            () -> assertEquals(NAME, spotDto.getName()),
            () -> assertEquals(DESCRIPTION, spotDto.getDescription()),
            () -> assertEquals(locationDto, spotDto.getLocation()),
            () -> assertEquals(categoryDto, spotDto.getCategory())
        );
    }
}
