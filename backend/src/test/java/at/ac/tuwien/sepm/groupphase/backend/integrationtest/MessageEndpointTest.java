package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MessageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SimpleUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
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

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageEndpoint messageEndpoint;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SimpleUserMapper simpleUserMapper;

    private ApplicationUser user1;
    private ApplicationUser user2;
    private ApplicationUser admin;
    private Spot spot;

    @BeforeEach
    public void beforeEach() {
        user1 = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(true)
            .name("user1")
            .password(PASSWORD)
            .build();
        userRepository.save(user1);

        user2 = ApplicationUser.builder()
            .email(EMAIL2)
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

        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        category = categoryRepository.save(category);

        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        location = locationRepository.save(location);

        spot = Spot.builder()
            .name(NAME)
            .description(DESCRIPTION)
            .location(location)
            .category(category)
            .owner(user1)
            .build();
        spotRepository.save(spot);
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

    @Test
    public void findBySpot_nonexistentSpotId() throws Exception {
        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", "200")
                .queryParam("size", "26")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andExpect(status().isNotFound());

        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", "3")
                .queryParam("size", "5")
                .queryParam("page", "1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void findBySpot_pageSizing() throws Exception {
        List<Message> messageList = new ArrayList<>();

        // Save 26 Messages with content A..Z
        IntStream.rangeClosed('A', 'Z').forEach(letter -> {
            Message message = Message.builder()
                .owner(user1)
                .content(String.valueOf((char) letter))
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
    public void create_nonexistentSpotId() throws Exception {
        MessageDto messageDto = MessageDto.builder()
            .content("Hi")
            .spotId(100L).build();

        mockMvc
            .perform(post(MESSAGE_BASE_URI + "/")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void create_BasicPositiveTests() throws Exception {
        int messageCount = 100;
        List<MessageDto> messageDtoList = new ArrayList<>();

        for (int i = 0; i < messageCount; i++) {
            messageDtoList.add(MessageDto.builder()
                .content("Message 1")
                .spotId(spot.getId()).build());
        }

        for (var messageDto : messageDtoList) {
            mockMvc
                .perform(post(MESSAGE_BASE_URI + "/")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isCreated());
        }

        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/")
                .param("spotId", spot.getId().toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(messageCount));
    }

    @Test
    public void getById_nonexistentId() throws Exception {
        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES))) // TODO: remove when guest work
            .andExpect(status().isNotFound());
    }

    @Test
    public void getById_basicPositive() throws Exception {
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
    public void deleteById_wrongId() throws Exception {
        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andExpect(status().isNotFound());

        MessageDto messageDto = MessageDto.builder()
            .content("Hi")
            .spotId(spot.getId()).build();

        MvcResult result = mockMvc
            .perform(post(MESSAGE_BASE_URI + "/")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDto)))
            .andExpect(status().isCreated())
            .andReturn();

        messageDto = objectMapper.readValue(result.getResponse().getContentAsString(), MessageDto.class);

        assertEquals(1, messageRepository.findAll().size());

        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/" + messageDto.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Hi"));

        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/" + (messageDto.getId() + 1))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
            .andExpect(status().isNotFound());

        assertTrue(messageRepository.findAll().size() == 1);
    }

    @Test
    public void deleteById_AsGuest() throws Exception {
        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteById_foreignMessage() throws Exception {
        Message message = messageRepository.save(Message.builder()
            .owner(user1)
            .content(MESSAGE_CONTENT)
            .spot(spot)
            .publishedAt(LocalDateTime.now()).build());

        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/" + message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user2.getEmail(), USER_ROLES)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteById_ownMessage() throws Exception {
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
    public void deleteById_asAdmin() throws Exception {
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
    @WithMockUser(roles = "ADMIN")
    public void filterAllMessagesWithCategoryHashtagUserTime() throws Exception {
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
            .owner(user1)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message message = Message.builder()
            .owner(user1)
            .spot(spot)
            .downVotes(ZERO)
            .upVotes(ZERO)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message);
        Message message2 = Message.builder()
            .owner(user1)
            .spot(spot)
            .downVotes(ZERO)
            .upVotes(ZERO)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message2);
        Hashtag hashtag = Hashtag.builder()
            .name("test")
            .messagesList(Arrays.asList(message))
            .spotsList(Arrays.asList(spot))
            .build();
        hashtagRepository.save(hashtag);
        MvcResult mvcResult = this.mockMvc.perform(
            get("/api/v1/messages/filter?categoryMes="+category.getId()+"&hashtag="+hashtag.getName()+"&user="+user1.getName()+"&time=1000-01-01")
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("\"totalElements\":1"));
    }
    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void messageMustNotBeSpaces() {
        MessageDto message = MessageDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user1))
            .content("       ")
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> messageEndpoint.create(message));
        assertAll(
            () -> assertEquals(0, messageRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "422 UNPROCESSABLE_ENTITY \"Message content must not only consist of white space characters!\"")
        );
    }
    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "USER")
    public void messageMustNotExpireInThePast() {
        MessageDto message = MessageDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(user1))
            .content(MESSAGE_CONTENT)
            .expirationDate(DATE_IN_THE_PAST)
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> messageEndpoint.create(message));
        assertAll(
            () -> assertEquals(0, messageRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "422 UNPROCESSABLE_ENTITY \"Message expiration date must be in the future!\"")
        );
    }
}
