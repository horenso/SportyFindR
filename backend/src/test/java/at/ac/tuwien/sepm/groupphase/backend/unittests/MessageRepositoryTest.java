package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import org.junit.jupiter.api.AfterEach;
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
    @Autowired
    private  SpotRepository spotRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;
    @Test
    public void givenNothing_whenSaveMessage_thenFindListWithOneElementAndFindMessageById() {
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        category = categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        location = locationRepository.save(location);
        Spot spot= Spot.builder()
            .owner(user)
            .name(SPOT_NAME)
            .description(SPOT_DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spot = spotRepository.save(spot);

        Message message = Message.builder()
            .spot(spot)
            .owner(user)
            .content(TEST_NEWS_TITLE)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();

        messageRepository.save(message);

        assertAll(
            () -> assertEquals(1, messageRepository.findAll().size()),
            () -> assertNotNull(messageRepository.findById(message.getId()))
        );
    }
     @AfterEach
    public void afterEach(){
         messageRepository.deleteAll();
         spotRepository.deleteAll();
         locationRepository.deleteAll();
         categoryRepository.deleteAll();
         userRepository.deleteAll();
     }

}
