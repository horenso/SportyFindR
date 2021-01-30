package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MessageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SimpleUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureMockMvc
public class MessageEndpointTest implements TestData {

    @Autowired private MessageRepository messageRepository;
    @Autowired private MessageEndpoint messageEndpoint;
    @Autowired private SpotRepository spotRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SimpleUserMapper simpleUserMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    @Autowired private MessageService messageService;
    @Autowired private SecurityProperties securityProperties;
    @Autowired private JwtTokenizer jwtTokenizer;
    @Autowired private MockMvc mockMvc;

    private ApplicationUser user1;
    private ApplicationUser user2;
    private ApplicationUser admin;
    private Spot spot;

    @BeforeEach
    public void beforeEach() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);

        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);

        spot = Spot.builder()
            .owner(user1)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);

        user1 = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(true)
            .name("user1")
            .password(PASSWORD)
            .build();
        userRepository.save(user1);

        user2 = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(true)
            .name("user2")
            .password(PASSWORD2)
            .build();
        userRepository.save(user2);

        admin = ApplicationUser.builder()
            .email(ADMIN_USER)
            .enabled(true)
            .name("admin")
            .password(PASSWORD3)
            .build();
        userRepository.save(admin);
    }

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
    public void findMessageByNonexistentId() throws Exception {
        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andExpect(status().isNotFound());
    }

    @Test
    public void findMessageById() throws Exception {
        Message message = Message.builder()
            .owner(user1)
            .content(MESSAGE_CONTENT)
            .spot(spot)
            .publishedAt(LocalDateTime.now()).build();
        message = messageRepository.save(message);

        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/" + message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(message.getId()))
            .andExpect(jsonPath("$.content").value(message.getContent()))
            .andExpect(jsonPath("$.owner.name").value(user1.getName()));
    }

    @Test
    public void getMessageByNonexistentId() throws Exception {
        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", "200")
                .queryParam("size", "26")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andExpect(status().isNotFound());
    }

    @Test
    public void getPagedMessages() throws Exception {
        List<Message> messageList = new ArrayList<>();

        // Save 26 Messages with content A..Z
        IntStream.rangeClosed('A', 'Z').forEach(letter -> {
            Message message = Message.builder()
                .owner(user1)
                .content(String.valueOf((char)letter))
                .spot(spot)
                .publishedAt(LocalDateTime.now()).build();
            messageList.add(messageRepository.save(message));
        });

        // Get all Messages
        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", spot.getId().toString())
                .queryParam("size", "26")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(26)))
            .andExpect(jsonPath("$.numberOfElements").value(26))
            .andExpect(jsonPath("$.last").value(true));

        // Get the first 5 Messages
        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", spot.getId().toString())
                .queryParam("size", "5")
                .queryParam("page", "0")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)))
            .andExpect(jsonPath("$.numberOfElements").value(5))
            .andExpect(jsonPath("$.last").value(false))
            .andExpect(jsonPath("$.content[0].content").value("A"))
            .andExpect(jsonPath("$.content[1].content").value("B"))
            .andExpect(jsonPath("$.content[4].content").value("E"));

        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", spot.getId().toString())
                .queryParam("size", "5")
                .queryParam("page", "1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)))
            .andExpect(jsonPath("$.numberOfElements").value(5))
            .andExpect(jsonPath("$.last").value(false))
            .andExpect(jsonPath("$.content[0].content").value("F"))
            .andExpect(jsonPath("$.content[4].content").value("J"));

        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", spot.getId().toString())
                .queryParam("size", "5")
                .queryParam("page", "5")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(jsonPath("$.last").value(true))
            .andExpect(jsonPath("$.content[0].content").value("Z"))
            .andExpect(jsonPath("$.content[0].owner.name").value(user1.getName()));
    }

    @Test
    public void deleteNonexistentMessage() throws Exception {
        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAsGuest() throws Exception {
        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteForeignMessage() throws Exception {
        messageRepository.save(Message.builder()
            .owner(user1)
            .content(MESSAGE_CONTENT)
            .spot(spot)
            .publishedAt(LocalDateTime.now()).build());

        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user2.getEmail(), USER_ROLES)))
            .andExpect(status().isUnauthorized());
    }


    @Test
    public void deleteOwnMessage() throws Exception {
        Message message = Message.builder()
            .owner(user1)
            .content(MESSAGE_CONTENT)
            .spot(spot)
            .publishedAt(LocalDateTime.now()).build();
        message = messageRepository.save(message);

        // Get message by id
        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/" + message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.owner.name").value(user1.getName()));

        // Delete the message
        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/" + message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
            .andExpect(status().isOk());

        // Now there should be no message returned
        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/" + message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
            .andExpect(status().isNotFound());

        // And it should not be in the database
        assertTrue(this.messageRepository.findById(message.getId()).isEmpty());
    }

    @Test
    public void deleteMessageAsAdmin() throws Exception {
        Message message = Message.builder()
            .owner(user1)
            .content(MESSAGE_CONTENT)
            .spot(spot)
            .publishedAt(LocalDateTime.now()).build();
        message = messageRepository.save(message);

        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/" + message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(admin.getEmail(), ADMIN_ROLES)))
            .andExpect(status().isOk());

        assertTrue(messageRepository.findById(message.getId()).isEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createMessageWithWrongSpotIdTest() throws Exception {
        MessageDto messageDto = MessageDto.builder()
            .content("Hi")
            .spotId(100L).build();

        mockMvc
            .perform(post(MESSAGE_BASE_URI + "/")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(messageDto)))
            .andExpect(status().isNotFound());
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
        Throwable e = assertThrows(ResponseStatusException.class, () -> messageEndpoint.findBySpot(ID, 2, 2));
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
