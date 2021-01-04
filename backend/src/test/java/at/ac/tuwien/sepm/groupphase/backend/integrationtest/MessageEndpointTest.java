package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MessageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MessageEndpointTest implements TestData {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageEndpoint messageEndpoint;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    public void afterEach(){
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createMessageTest() {
        Category category = Category.builder()
            .name(CAT_NAME)
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
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        MessageDto messageDto = MessageDto.builder()
            .spotId(spot.getId())
            .content(MESSAGE_CONTENT)
            .build();
        MessageDto messageDto1 = messageEndpoint.create(messageDto);
        messageDto.setId(messageDto1.getId());
        messageDto.setPublishedAt(messageDto1.getPublishedAt());
        Optional<Message> message = messageRepository.findById(messageDto.getId());
        assertAll(
            () -> assertNotEquals(null, message),
            () -> assertEquals(messageDto.getId(), message.get().getId()),
            () -> assertEquals(messageDto.getContent(), message.get().getContent()),
            () -> assertEquals(messageDto.getSpotId(), message.get().getSpot().getId()),
            () -> assertEquals(messageDto.getPublishedAt().truncatedTo(ChronoUnit.MILLIS),message.get().getPublishedAt().truncatedTo(ChronoUnit.MILLIS)),
            () -> assertEquals(messageDto.getDownVotes(),message.get().getDownVotes()),
            () -> assertEquals(messageDto.getUpVotes(), message.get().getUpVotes())
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteMessageTest() {
        Category category = Category.builder()
            .name(CAT_NAME)
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
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        Message message = Message.builder()
            .spot(spot)
            .content(MESSAGE_CONTENT)
            .downVotes(0)
            .publishedAt(LocalDateTime.of(2021,01,04,18,19,20,100))
            .build();
        messageRepository.save(message);
        List<Message> messages1 = messageRepository.findAll();
        assertAll(
            () -> assertFalse(messages1.isEmpty())
        );
        messageEndpoint.deleteById(message.getId());
        List<Message> messages2 = messageRepository.findAll();
        assertAll(
            () -> assertTrue(messages2.isEmpty())
        );
    }
    //negative tests
    //TODO: negative create message test?
    //TODO: negative delete message
}
