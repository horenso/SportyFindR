package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.ReactionEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReactionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ReactionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SimpleUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.ReactionService;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.HashSet;
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
    private UserRepository userRepository;
    @Autowired
    private ReactionMapper reactionMapper;
    private ApplicationUser user;
    @Autowired
    private SimpleUserMapper simpleUserMapper;
    @Autowired
    private RoleService roleService;
    private HashSet<Role> roles = new HashSet<>();
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void beforeEach() throws ValidationException {
        Role role = Role.builder()
            .name("USER")
            .build();
        role = roleService.create(role);
        this.roles.add(role);

        this.user = ApplicationUser.builder()
            .roles(roles)
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
    }


    @AfterEach
    public void afterEach() {
        reactionRepository.deleteAll();
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    //positive tests
    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void createReaction() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .owner(user)
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        ReactionDto reactionDto = ReactionDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user))
            .messageId(msg.getId())
            .type(ReactionDto.ReactionDtoType.THUMBS_DOWN)
            .build();
        reactionDto.setId(reactionEndpoint.create(reactionDto).getId());
        List<Reaction> reactions = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(1, reactions.size()),
            () -> assertEquals(reactionDto.getId(), reactions.get(0).getId()),
            () -> assertEquals(reactionDto.getType(), reactionMapper.reactionToReactionDto(reactions.get(0)).getType()),
            () -> assertEquals(reactionDto.getMessageId(), reactions.get(0).getMessage().getId()),
            () -> assertEquals(reactionDto.getOwner(), simpleUserMapper.userToSimpleUserDto(reactions.get(0).getOwner()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void changeReaction() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .owner(user)
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        Reaction reaction = Reaction.builder()
            .owner(user)
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
            () -> assertEquals(reaction.getMessage().getId(), reactions1.get(0).getMessage().getId()),
            () -> assertEquals(reaction.getOwner(), reactions1.get(0).getOwner())
        );
        ReactionDto reactionDto = ReactionDto.builder()
            .messageId(msg.getId())
            .type(ReactionDto.ReactionDtoType.THUMBS_UP)
            .id(reaction.getId())
            .build();
        reactionEndpoint.change(reactionDto).getOwner();
        reactionDto.setOwner(simpleUserMapper.userToSimpleUserDto(user));
        List<Reaction> reactions2 = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(1, reactions2.size()),
            () -> assertEquals(reactionDto.getId(), reactions2.get(0).getId()),
            () -> assertEquals(reactionDto.getType(), reactionMapper.reactionToReactionDto(reactions2.get(0)).getType()),
            () -> assertEquals(reactionDto.getMessageId(), reactions2.get(0).getMessage().getId()),
            () -> assertEquals(reactionDto.getOwner(), simpleUserMapper.userToSimpleUserDto(reactions2.get(0).getOwner()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void getReactionByMessage() {
        ApplicationUser user2 = ApplicationUser.builder()
            .email(EMAIL2)
            .enabled(ENABLED)
            .name(USERNAME2)
            .password(PASSWORD2)
            .roles(roles)
            .build();
        userRepository.save(user2);
        ApplicationUser user3 = ApplicationUser.builder()
            .email(EMAIL3)
            .enabled(ENABLED)
            .name(USERNAME3)
            .password(PASSWORD3)
            .roles(roles)
            .build();
        userRepository.save(user3);
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .owner(user)
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        Reaction reaction = Reaction.builder()
            .owner(user)
            .message(msg)
            .type(Reaction.ReactionType.THUMBS_DOWN)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
        reactionRepository.save(reaction);
        Reaction reaction2 = Reaction.builder()
            .owner(user2)
            .message(msg)
            .type(Reaction.ReactionType.THUMBS_DOWN)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
        reactionRepository.save(reaction2);
        Reaction reaction3 = Reaction.builder()
            .owner(user3)
            .message(msg)
            .type(Reaction.ReactionType.THUMBS_UP)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
        reactionRepository.save(reaction3);
        List<ReactionDto> reactions = reactionEndpoint.getReactionsByMessage(msg.getId());
        assertAll(
            () -> assertEquals(3, reactions.size()),
            () -> assertEquals(reaction.getId(), reactions.get(0).getId()),
            () -> assertEquals(reaction.getType(), reactionMapper.reactionDtoTypeToReactionType(reactions.get(0).getType())),
            () -> assertEquals(reaction.getMessage().getId(), reactions.get(0).getMessageId()),
            () -> assertEquals(simpleUserMapper.userToSimpleUserDto(reaction.getOwner()), reactions.get(0).getOwner()),
            () -> assertEquals(reaction2.getId(), reactions.get(1).getId()),
            () -> assertEquals(reaction2.getType(), reactionMapper.reactionDtoTypeToReactionType(reactions.get(1).getType())),
            () -> assertEquals(reaction2.getMessage().getId(), reactions.get(1).getMessageId()),
            () -> assertEquals(simpleUserMapper.userToSimpleUserDto(reaction2.getOwner()), reactions.get(1).getOwner()),
            () -> assertEquals(reaction3.getId(), reactions.get(2).getId()),
            () -> assertEquals(reaction3.getType(), reactionMapper.reactionDtoTypeToReactionType(reactions.get(2).getType())),
            () -> assertEquals(reaction3.getMessage().getId(), reactions.get(2).getMessageId()),
            () -> assertEquals(simpleUserMapper.userToSimpleUserDto(reaction3.getOwner()), reactions.get(2).getOwner())
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void deleteReaction() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .owner(user)
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        Reaction reaction = Reaction.builder()
            .owner(user)
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
            () -> assertEquals(reaction.getMessage().getId(), reactions1.get(0).getMessage().getId()),
            () -> assertEquals(reaction.getOwner(), reactions1.get(0).getOwner())

        );
        reactionEndpoint.deleteById(reaction.getId());
        List<Reaction> reactions2 = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(0, reactions2.size())
        );
    }

    //negative Tests
    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void createReactionWithoutValidCategory() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .owner(user)
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        ReactionDto reactionDto = ReactionDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user))
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
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void changeReactionWithWrongId() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .owner(user)
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        Reaction reaction = Reaction.builder()
            .owner(user)
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
            () -> assertEquals(reaction.getMessage().getId(), reactions1.get(0).getMessage().getId()),
            () -> assertEquals(reaction.getOwner(), reactions1.get(0).getOwner())

        );
        ReactionDto reactionDto = ReactionDto.builder()
            .messageId(msg.getId() + 1)
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
            () -> assertEquals(reaction.getMessage().getId(), reactions2.get(0).getMessage().getId()),
            () -> assertEquals(reaction.getOwner(), reactions2.get(0).getOwner())

        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void getReactionByMessageWithWrongId() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .owner(user)
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        Reaction reaction = Reaction.builder()
            .owner(user)
            .message(msg)
            .type(Reaction.ReactionType.THUMBS_DOWN)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
        reactionRepository.save(reaction);
        Long id = msg.getId() + 1;
        Throwable e = assertThrows(ResponseStatusException.class, () -> reactionEndpoint.getReactionsByMessage(id));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Message with ID " + id + " cannot be found!\"")
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void deleteReactionWithWrongId() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message msg = Message.builder()
            .owner(user)
            .content(TEST_NEWS_TEXT)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .spot(spot)
            .build();
        messageRepository.save(msg);
        Reaction reaction = Reaction.builder()
            .owner(user)
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
            () -> assertEquals(reaction.getMessage().getId(), reactions1.get(0).getMessage().getId()),
            () -> assertEquals(reaction.getOwner(), reactions1.get(0).getOwner())

        );
        Long id = reaction.getId() + 1;
        Throwable e = assertThrows(ResponseStatusException.class, () -> reactionEndpoint.deleteById(id));
        assertAll(
            () -> assertEquals(1, reactionRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Reaction with id " + id + " not found.\"")
        );
        List<Reaction> reactions2 = reactionRepository.findAll();
        assertAll(
            () -> assertEquals(1, reactions2.size()),
            () -> assertEquals(reaction.getId(), reactions2.get(0).getId()),
            () -> assertEquals(reaction.getType(), (reactions2.get(0).getType())),
            () -> assertEquals(reaction.getMessage().getId(), reactions2.get(0).getMessage().getId()),
            () -> assertEquals(reaction.getOwner(), reactions2.get(0).getOwner())

        );
    }
}

