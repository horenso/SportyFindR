package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class MessageRepositoryTest implements TestData {

    @Autowired
    private MessageRepository messageRepository;
    private SpotRepository spotRepository;
    private CategoryRepo

    @Before
    public void before() {
        Category category = Category.CategoryBuilder.aCategory()
            .id(ID)
            .build();
        Location location = Location.LocationBuilder.aLocation()
            .latitude(10.0)
            .longitude(10.0)
            .build();
        Spot spot= Spot.builder()
            .name(SPOT_NAME)
            .description(SPOT_DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        category
        spotRepository.save()
    }
    @Test
    public void givenNothing_whenSaveMessage_thenFindListWithOneElementAndFindMessageById() {
        Message message = Message.builder()
            .content(TEST_NEWS_TITLE)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();

        messageRepository.save(message);

        assertAll(
            () -> assertEquals(1, messageRepository.findAll().size()),
            () -> assertNotNull(messageRepository.findById(message.getId()))
        );
    }

}
