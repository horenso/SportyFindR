package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
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
                .latitude(91.57)
                .longitude(-20.3)
                .build()
        );

        Category cat = categoryRepository.save(
            Category.builder()
                .name("foo")
                .build()
        );

        Spot spot = Spot.builder()
            .name("bar")
            .description("Lorem ipsum")
            .location(loc)
            .category(cat)
            .build();
        this.msg = Message.builder()
            .content("dolor")
            .publishedAt(LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0))
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
            .publishedAt(LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0))
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
            .publishedAt(LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0))
            .message(msg)
            .build();

        Long msgId = msg.getId();
        Reaction createReaction = reactionService.create(rct);
        Reaction reaction = reactionService.getReactionsByMessageId(msgId).get(0); // should be the first in the list
        assertAll(
            () -> assertTrue(reaction.getId() > 0),
            () -> assertEquals(rct.getType(), reaction.getType()),
            () -> assertEquals(rct.getMessage().getId(), reaction.getMessage().getId()),
            () -> assertEquals(rct.getPublishedAt(), reaction.getPublishedAt())
        );
    }

    @Test
    public void reactionThrowExceptionByIncorrectMessageId() {

        Reaction rct = Reaction.builder()
            .type(Reaction.ReactionType.THUMBS_UP)
            .publishedAt(LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0))
            .message(msg)
            .build();

        Long msgId = msg.getId();

        reactionService.create(rct);
        assertAll(
            () -> assertThrows(NotFoundException.class, () -> reactionService.getReactionsByMessageId(msgId + 10000).get(0))
        );
    }
}
