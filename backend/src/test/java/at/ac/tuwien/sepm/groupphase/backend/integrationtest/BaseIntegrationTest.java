package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MessageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SimpleUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class BaseIntegrationTest implements TestData {
    @Autowired
    protected MessageRepository messageRepository;
    @Autowired
    protected MessageEndpoint messageEndpoint;
    @Autowired
    protected SpotRepository spotRepository;
    @Autowired
    protected HashtagRepository hashtagRepository;
    @Autowired
    protected LocationRepository locationRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected SecurityProperties securityProperties;
    @Autowired
    protected JwtTokenizer jwtTokenizer;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected SimpleUserMapper simpleUserMapper;
    @Autowired
    protected ReactionRepository reactionRepository;

    protected ApplicationUser user1;
    protected ApplicationUser user2;
    protected ApplicationUser admin;
    protected Category category;
    protected Location location;
    protected Spot spot1;
    protected Spot spot2;

    @BeforeEach
    public void beforeEach() {
        user1 = ApplicationUser.builder()
            .email(EMAIL4)
            .enabled(true)
            .name(USERNAME4)
            .password(PASSWORD4)
            .build();
        userRepository.save(user1);

        user2 = ApplicationUser.builder()
            .email(EMAIL5)
            .enabled(true)
            .name(USERNAME5)
            .password(PASSWORD5)
            .build();
        userRepository.save(user2);

        admin = ApplicationUser.builder()
            .email(ADMIN_USER)
            .enabled(true)
            .name("admin")
            .password(PASSWORD3)
            .build();
        userRepository.save(admin);

        category = Category.builder()
            .name(CAT_NAME)
            .build();
        category = categoryRepository.save(category);

        location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        location = locationRepository.save(location);

        spot1 = Spot.builder()
            .name("Spot 1")
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .owner(user1)
            .build();
        spotRepository.save(spot1);

        spot2 = Spot.builder()
            .name("Spot 2")
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .owner(user1)
            .build();
        spotRepository.save(spot2);
    }

    @AfterEach
    public void afterEach() {
        hashtagRepository.deleteAll();
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
