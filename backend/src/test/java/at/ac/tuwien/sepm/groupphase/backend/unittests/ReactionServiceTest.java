package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleReactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReactionServiceTest implements TestData {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReactionRepository reactionRepository;
    @Autowired
    private ReactionService reactionService;

//    private final ReactionService reactionService = new SimpleReactionService(reactionRepository, messageRepository);

    private Message msg;

    @BeforeEach
    public void beforeEach() {
        Location loc = locationRepository.save(
            Location.builder()
                .latitude(LAT3)
                .longitude(LONG3)
                .build()
        );

        Category cat = categoryRepository.save(
            Category.builder()
                .name(CAT_NAME)
                .build()
        );

        Spot spot = Spot.builder()
            .name(SPOT_NAME)
            .description(SPOT_DESCRIPTION)
            .location(loc)
            .category(cat)
            .build();
        this.msg = Message.builder()
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .spot(spot)
            .build();

        spotRepository.save(spot);
        messageRepository.save(msg);
    }

    @AfterEach
    public void afterEach() {
        reactionRepository.deleteAll();
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        // ToDo: Remove everything
    }

    @Test
    public void reactionCreateReturnsReaction() throws NotFoundException2{

        Reaction rct = Reaction.builder()
            .type(Reaction.ReactionType.THUMBS_UP)
            .publishedAt(DATE2)
            .message(msg)
            .build();

        Reaction reaction = reactionService.create(rct);
        assertAll(
            () -> assertTrue(reaction.getId() > 0),
            () -> assertEquals(rct.getType(), reaction.getType()),
            () -> assertEquals(rct.getMessage(), reaction.getMessage()),
            () -> assertEquals(rct.getPublishedAt(), reaction.getPublishedAt())
        );
    }

    @Test
    public void reactionFindReactionByMessageId() throws NotFoundException2 {

        Reaction rct = Reaction.builder()
            .type(Reaction.ReactionType.THUMBS_UP)
            .message(msg)
            .build();

        Long msgId = msg.getId();
        Reaction createReaction = reactionService.create(rct);
        Reaction reaction = reactionService.getReactionsByMessageId(msgId).get(0); // should be the first in the list
        assertAll(
            () -> assertTrue(reaction.getId() > 0),
            () -> assertEquals(rct.getType(), reaction.getType()),
            () -> assertEquals(rct.getMessage().getId(), reaction.getMessage().getId()),
            () -> assertEquals(rct.getPublishedAt().truncatedTo(ChronoUnit.MILLIS), reaction.getPublishedAt().truncatedTo(ChronoUnit.MILLIS))
        );
    }

    @Test
    public void reactionThrowExceptionByIncorrectMessageId() throws NotFoundException2{

        Reaction rct = Reaction.builder()
            .type(Reaction.ReactionType.THUMBS_UP)
            .publishedAt(DATE)
            .message(msg)
            .build();

        Long msgId = msg.getId();

        reactionService.create(rct);
        assertAll(
            () -> assertThrows(NotFoundException2.class, () -> reactionService.getReactionsByMessageId(msgId + 10000).get(0))
        );
    }
}
