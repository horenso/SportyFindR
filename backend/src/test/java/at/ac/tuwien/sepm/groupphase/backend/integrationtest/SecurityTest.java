package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Security is a cross-cutting concern, however for the sake of simplicity it is tested against the message endpoint
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    private final HashSet<Role> roles = new HashSet<>();
    private Long id;
    private ApplicationUser user;
    private Role role;
    private Message message = Message.builder()
        .content(TEST_NEWS_SUMMARY)
        .publishedAt(TEST_NEWS_PUBLISHED_AT)
        .build();

    @BeforeEach
    public void beforeEach() throws ValidationException {
        messageRepository.deleteAll();
        role = Role.builder()
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
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        category = categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        location = locationRepository.save(location);
        Spot spot = Spot.builder()
            .owner(user)
            .name(SPOT_NAME)
            .description(SPOT_DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spot = spotRepository.save(spot);
        id= spot.getId();
        message = Message.builder()
            .owner(user)
            .spot(spot)
            .content(TEST_NEWS_TITLE)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
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
    public void givenUserLoggedIn_whenFindAll_then200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "?spotId=" + id)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }


    @Test
    public void givenAdminLoggedIn_whenPost_then201() throws Exception {
        MessageDto messageDto = messageMapper.messageToMessageDto(message);
        String body = objectMapper.writeValueAsString(messageDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void givenNoOneLoggedIn_whenPost_then401() throws Exception {
        message.setPublishedAt(null);
        MessageDto messageDto = messageMapper.messageToMessageDto(message);
        String body = objectMapper.writeValueAsString(messageDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void givenUserLoggedIn_whenPost_then201() throws Exception {
        message.setPublishedAt(null);
        MessageDto messageDto = messageMapper.messageToMessageDto(message);
        String body = objectMapper.writeValueAsString(messageDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }
}
