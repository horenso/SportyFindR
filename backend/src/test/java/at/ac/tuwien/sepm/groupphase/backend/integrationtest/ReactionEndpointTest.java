package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReactionEndpointTest implements TestData {

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
            .latitude(LOCATION.getLatitude())
            .longitude(LOCATION.getLongitude())
            .build();
        Spot spot = Spot.builder()
            .id(ID)
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

    // ToDo: write Endpoint Test
}
