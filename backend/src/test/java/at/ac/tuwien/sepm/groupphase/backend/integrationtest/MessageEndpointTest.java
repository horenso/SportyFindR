package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MessageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SimpleUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MessageEndpointTest implements TestData {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageEndpoint messageEndpoint;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SimpleUserMapper simpleUserMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MessageService messageService;


    @AfterEach
    public void afterEach() {
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles= "USER")
    public void createMessageTest() {
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
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        MessageDto messageDto = MessageDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user))
            .spotId(spot.getId())
            .content(MESSAGE_CONTENT)
            .build();
        MessageDto messageDto1 = messageEndpoint.create(messageDto);
        messageDto.setId(messageDto1.getId());
        messageDto.setPublishedAt(messageDto1.getPublishedAt());
        List<Message> message = messageRepository.findAll();
        assertAll(
            () -> assertEquals(messageDto.getId(), message.get(0).getId()),
            () -> assertEquals(messageDto.getContent(), message.get(0).getContent()),
            () -> assertEquals(messageDto.getSpotId(), message.get(0).getSpot().getId()),
            () -> assertEquals(messageDto.getPublishedAt().truncatedTo(ChronoUnit.MILLIS), message.get(0).getPublishedAt().truncatedTo(ChronoUnit.MILLIS)),
            () -> assertEquals(messageDto.getDownVotes(), message.get(0).getDownVotes()),
            () -> assertEquals(messageDto.getUpVotes(), message.get(0).getUpVotes()),
            () -> assertEquals(messageDto.getOwner(), simpleUserMapper.userToSimpleUserDto(message.get(0).getOwner()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles= "USER")
    public void deleteMessageTest() throws Exception {
        Role role = Role.builder()
            .name("ADMIN")
            .build();
        role = roleService.create(role);
        Role role2 = Role.builder()
            .name("USER")
            .build();
        role2 = roleService.create(role2);
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        roles.add(role2);
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .roles(roles)
            .build();
        user= userService.createApplicationUser(user);
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        Message message = Message.builder()
            .owner(user)
            .spot(spot)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message);
        List<Message> messages1 = messageRepository.findAll();
        assertAll(
            () -> assertFalse(messages1.isEmpty())
        );
        messageEndpoint.deleteById(message.getId());
        List<Message> messages2 = messageRepository.findAll();
        assertAll(
            () -> assertTrue(messages2.isEmpty())
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD)
    public void findMessagesBySpotTest() throws NotFoundException2, ValidationException {
        Role role = Role.builder()
            .name("ADMIN")
            .build();
        role = roleService.create(role);
        Role role2 = Role.builder()
            .name("USER")
            .build();
        role2 = roleService.create(role2);
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        roles.add(role2);
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .roles(roles)
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        Message message = Message.builder()
            .spot(spot)
            .downVotes(ZERO)
            .upVotes(ZERO)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageService.create(message);
        Message message2 = Message.builder()
            .spot(spot)
            .downVotes(ZERO)
            .upVotes(ZERO)
            .content(CAT_NAME)
            .publishedAt(DATE2)
            .build();
        messageService.create(message2);
        List<MessageDto> messages = messageEndpoint.findBySpotOld(spot.getId());
        assertAll(
            () -> assertEquals(message.getId(), messages.get(0).getId()),
            () -> assertEquals(message.getContent(), messages.get(0).getContent()),
            () -> assertEquals(message.getSpot().getId(), messages.get(0).getSpotId()),
            () -> assertEquals(message.getPublishedAt().truncatedTo(ChronoUnit.MILLIS), messages.get(0).getPublishedAt().truncatedTo(ChronoUnit.MILLIS)),
            () -> assertEquals(message.getDownVotes(), messages.get(0).getDownVotes()),
            () -> assertEquals(message.getUpVotes(), messages.get(0).getUpVotes()),
            () -> assertEquals(simpleUserMapper.userToSimpleUserDto(message.getOwner()),(messages.get(0).getOwner())),
            () -> assertEquals(message2.getId(), messages.get(1).getId()),
            () -> assertEquals(message2.getContent(), messages.get(1).getContent()),
            () -> assertEquals(message2.getSpot().getId(), messages.get(1).getSpotId()),
            () -> assertEquals(message2.getPublishedAt().truncatedTo(ChronoUnit.MILLIS), messages.get(1).getPublishedAt().truncatedTo(ChronoUnit.MILLIS)),
            () -> assertEquals(message2.getDownVotes(), messages.get(1).getDownVotes()),
            () -> assertEquals(message2.getUpVotes(), messages.get(1).getUpVotes()),
            () -> assertEquals(simpleUserMapper.userToSimpleUserDto(message2.getOwner()),(messages.get(1).getOwner()))
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getMessageByIdTest() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        Message message = Message.builder()
            .owner(user)
            .spot(spot)
            .upVotes(ZERO)
            .downVotes(ZERO)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message);
        MessageDto messageDto = messageEndpoint.getById(message.getId());
        assertAll(
            () -> assertEquals(message.getId(), messageDto.getId()),
            () -> assertEquals(message.getContent(), messageDto.getContent()),
            () -> assertEquals(message.getSpot().getId(), message.getSpot().getId()),
            () -> assertEquals(message.getPublishedAt().truncatedTo(ChronoUnit.MILLIS), messageDto.getPublishedAt().truncatedTo(ChronoUnit.MILLIS)),
            () -> assertEquals(message.getDownVotes(), messageDto.getDownVotes()),
            () -> assertEquals(message.getUpVotes(), messageDto.getUpVotes()),
            () -> assertEquals(message.getOwner(),simpleUserMapper.simpleUserDtoToUser(messageDto.getOwner()))

        );
    }

    //negative tests
    @Test
    @WithMockUser(roles = "USER")
    public void createMessageWithWrongSpotIdTest() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        MessageDto messageDto = MessageDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user))
            .spotId(spot.getId() + 1)
            .content(MESSAGE_CONTENT)
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> messageEndpoint.create(messageDto));
        assertAll(
            () -> assertEquals(0, messageRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Spot does not Exist\"")
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteMessageWithWrongIdTest() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name(USERNAME)
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        categoryRepository.save(category);
        locationRepository.save(location);
        spotRepository.save(spot);
        Message message = Message.builder()
            .owner(user)
            .spot(spot)
            .content(MESSAGE_CONTENT)
            .downVotes(ZERO)
            .upVotes(ZERO)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message);
        List<Message> messages1 = messageRepository.findAll();
        assertAll(
            () -> assertFalse(messages1.isEmpty())
        );
        Long id = message.getId() + 1;
        Throwable e = assertThrows(ResponseStatusException.class, () -> messageEndpoint.deleteById(id));
        assertAll(
            () -> assertEquals(1, messageRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"No message with id " + id + " found!\"")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getEmptyMessageListTest() {
        Throwable e = assertThrows(ResponseStatusException.class, () -> messageEndpoint.findBySpotOld(ID));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"Spot with id " + ID + " not found.\"")
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getNonExistingMessageByIdTest() {
        Throwable e = assertThrows(ResponseStatusException.class, () -> messageEndpoint.getById(ID));
        assertAll(
            () -> assertEquals(e.getMessage(), "404 NOT_FOUND \"No messages found\"")
        );

    }
}
