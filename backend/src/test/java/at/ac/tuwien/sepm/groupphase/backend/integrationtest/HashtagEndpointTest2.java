package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.HashtagEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MessageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.SpotEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.HashtagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SpotDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SimpleUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SpotMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HashtagEndpointTest2 implements TestData {

    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MessageEndpoint messageEndpoint;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SimpleUserMapper simpleUserMapper;
    @Autowired
    private HashtagEndpoint hashtagEndpoint;
    @Autowired
    private SpotEndpoint spotEndpoint;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private HashtagRepository hashtagRepository;

    @AfterEach
    public void afterEach() {
        hashtagRepository.deleteAll();
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD)
    public void findAllHashtags() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        LocationDto location = LocationDto.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name("owner")
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        SpotDto spot = SpotDto.builder()
            .name(NAME)
            .description(DESCRIPTION_WITH_HASHTAGS2)
            .location(location)
            .category(categoryMapper.categoryToCategoryDto(category))
            .build();
        spot=spotEndpoint.create(spot);
        MessageDto message = MessageDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user))
            .spotId(spot.getId())
            .downVotes(0)
            .upVotes(0)
            .content(DESCRIPTION_WITH_HASHTAGS)
            .publishedAt(DATE)
            .build();
        messageEndpoint.create(message);
        MessageDto message2 = MessageDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user))
            .spotId(spot.getId())
            .downVotes(0)
            .upVotes(0)
            .content(DESCRIPTION_WITH_HASHTAGS2)
            .publishedAt(DATE)
            .build();
        messageEndpoint.create(message2);
        MessageDto message3 = MessageDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user))
            .spotId(spot.getId())
            .downVotes(0)
            .upVotes(0)
            .content(DESCRIPTION_WITH_HASHTAGS)
            .publishedAt(DATE)
            .build();
        messageEndpoint.create(message3);
        List<HashtagDto> hashtags = hashtagEndpoint.getAll();
        assertAll(
            () -> assertEquals(2, hashtags.size()),
            () -> assertEquals("secondBest", hashtags.get(0).getName()),
            () -> assertEquals("best", hashtags.get(1).getName())
        );
    }
}
