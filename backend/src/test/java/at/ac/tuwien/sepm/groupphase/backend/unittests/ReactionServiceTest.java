package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class ReactionServiceTest implements TestData {

    @Autowired
    private static SpotRepository spotRepository;
    @Autowired
    private static MessageRepository messageRepository;
    @Autowired
    private ReactionService reactionService;

    private static Message msg;

    @BeforeAll
    public static void init() {
        Location loc = Location.builder()
            .withLatitude(LOCATION.getLatitude())
            .withLongitude(LOCATION.getLongitude())
            .build();
        Spot spot = Spot.builder()
            .withId(ID)
            .withName(NAME)
            .withDescription(DESCRIPTION)
            .withLocation(loc)
            .withCategory(CATEGORY)
            .build();
        msg = Message.builder()
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();

        spotRepository.save(spot);
        messageRepository.save(msg);
    }

    @Test
    public void reactionCreateReturnsReaction() {

        Reaction rct = Reaction.builder()
            .reactionType(Reaction.ReactionType.THUMBS_UP)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .message(msg)
            .build();

        Reaction reaction = reactionService.create(rct);
        assertAll(
            () -> assertTrue(reaction.getId() > 0),
            () -> assertEquals(rct.getReactionType(), reaction.getReactionType()),
            () -> assertEquals(rct.getMessage(), reaction.getMessage()),
            () -> assertEquals(rct.getPublishedAt(), reaction.getPublishedAt())
        );
    }
}
