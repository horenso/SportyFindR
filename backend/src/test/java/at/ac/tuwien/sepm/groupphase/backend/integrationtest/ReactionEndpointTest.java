package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.ReactionEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReactionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ReactionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ReactionEndpointTest implements TestData {

    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReactionService reactionService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ReactionEndpoint reactionEndpoint;
    @Autowired
    private ReactionRepository reactionRepository;
    @Autowired
    private ReactionMapper reactionMapper;


    @AfterEach
    public void afterEach() {
        reactionRepository.deleteAll();
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    //positive tests
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createReaction() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(10.0)
            .longitude(10.0)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        ReactionDto reactionDto = ReactionDto.builder()
            .messageId(msg.getId())
            .type(ReactionDto.ReactionDtoType.THUMBS_DOWN)
            .build();
        reactionDto.setId(reactionEndpoint.create(reactionDto).getId());
        List<Reaction> reactions = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(1, reactions.size()),
            () -> assertEquals(reactionDto.getId(), reactions.get(0).getId()),
            () -> assertEquals(reactionDto.getType(), reactionMapper.reactionToReactionDto(reactions.get(0)).getType()),
            () -> assertEquals(reactionDto.getMessageId(), reactions.get(0).getMessage().getId())
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateReaction() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(10.0)
            .longitude(10.0)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        Reaction reaction = Reaction.builder()
            .message(msg)
            .type(Reaction.ReactionType.THUMBS_DOWN)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
        reactionRepository.save(reaction);
        List<Reaction> reactions1 = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(1, reactions1.size()),
            () -> assertEquals(reaction.getId(), reactions1.get(0).getId()),
            () -> assertEquals(reaction.getType(), reactions1.get(0).getType()),
            () -> assertEquals(reaction.getMessage().getId(), reactions1.get(0).getMessage().getId())
        );
        ReactionDto reactionDto = ReactionDto.builder()
            .messageId(msg.getId())
            .type(ReactionDto.ReactionDtoType.THUMBS_UP)
            .id(reaction.getId())
            .build();
        reactionEndpoint.change(reactionDto);
        List<Reaction> reactions2 = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(1, reactions2.size()),
            () -> assertEquals(reactionDto.getId(), reactions2.get(0).getId()),
            () -> assertEquals(reactionDto.getType(), reactionMapper.reactionToReactionDto(reactions2.get(0)).getType()),
            () -> assertEquals(reactionDto.getMessageId(), reactions2.get(0).getMessage().getId())
        );
    }

    //negative Tests
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createReactionWithoutValidCategory() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(10.0)
            .longitude(10.0)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        ReactionDto reactionDto = ReactionDto.builder()
            .messageId(msg.getId() + 1)
            .type(ReactionDto.ReactionDtoType.THUMBS_DOWN)
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> reactionEndpoint.create(reactionDto));
        assertAll(
            () -> assertEquals(0, reactionRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Message does not Exist\"")
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateReactionWithWrongId() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(10.0)
            .longitude(10.0)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        Reaction reaction = Reaction.builder()
            .message(msg)
            .type(Reaction.ReactionType.THUMBS_DOWN)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
        reactionRepository.save(reaction);
        List<Reaction> reactions1 = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(1, reactions1.size()),
            () -> assertEquals(reaction.getId(), reactions1.get(0).getId()),
            () -> assertEquals(reaction.getType(), reactions1.get(0).getType()),
            () -> assertEquals(reaction.getMessage().getId(), reactions1.get(0).getMessage().getId())
        );
        ReactionDto reactionDto = ReactionDto.builder()
            .messageId(msg.getId()+1)
            .type(ReactionDto.ReactionDtoType.THUMBS_UP)
            .id(reaction.getId())
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> reactionEndpoint.create(reactionDto));
        assertAll(
            () -> assertEquals(1, reactionRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Message does not Exist\"")
        );
        List<Reaction> reactions2 = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(1, reactions2.size()),
            () -> assertEquals(reaction.getId(), reactions2.get(0).getId()),
            () -> assertEquals(reaction.getType(), (reactions2.get(0).getType())),
            () -> assertEquals(reaction.getMessage().getId(), reactions2.get(0).getMessage().getId())
        );
    }
}

