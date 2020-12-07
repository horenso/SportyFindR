package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReactionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.SimpleReactionService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReactionServiceTest implements TestData {

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
        Location loc = Location.builder()
            .latitude(LOCATION.getLatitude())
            .longitude(LOCATION.getLongitude())
            .build();
        Spot spot = Spot.builder()
            .name(NAME)
            .description(DESCRIPTION)
            .location(loc)
            .category(CATEGORY)
            .build();
        msg = Message.builder()
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();

        spotRepository.save(spot);
        messageRepository.save(msg);
    }

    @AfterEach
    public void afterEach() {
        // ToDo: Remove everything
    }

    @Test
    public void reactionCreateReturnsReaction() {

        Reaction rct = Reaction.builder()
            .type(Reaction.ReactionType.THUMBS_UP)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
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
    public void reactionFindReactionByMessageId() {

        Reaction rct = Reaction.builder()
            .type(Reaction.ReactionType.THUMBS_UP)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .message(msg)
            .build();

        reactionService.create(rct);
        Reaction reaction = reactionService.getReactionsByMessageId(ID).get(0); // should be the first in the list
        assertAll(
            () -> assertTrue(reaction.getId() > 0),
            () -> assertEquals(rct.getType(), reaction.getType()),
            () -> assertEquals(rct.getMessage(), reaction.getMessage()),
            () -> assertEquals(rct.getPublishedAt(), reaction.getPublishedAt())
        );
    }

    @Test
    public void reactionThrowExceptionByIncorrectMessageId() {

        Reaction rct = Reaction.builder()
            .type(Reaction.ReactionType.THUMBS_UP)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .message(msg)
            .build();

        reactionService.create(rct);
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> reactionService.getReactionsByMessageId(ID + 100).get(0))
        );
    }
}
