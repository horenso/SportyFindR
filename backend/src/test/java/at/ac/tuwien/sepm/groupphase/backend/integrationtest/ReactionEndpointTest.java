package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
    @Autowired
    private static CategoryRepository categoryRepository;
    @Autowired
    private static LocationRepository locationRepository;

    private static Message msg;

    @BeforeAll
    public static void init() {
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
            .id(ID)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        msg = Message.builder()
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();

        spotRepository.save(spot);
        messageRepository.save(msg);
    }
    @AfterAll
    public static void afterAll(){

        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
    }

    // ToDo: write Endpoint Test
}
