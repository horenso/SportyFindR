package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
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

import static org.junit.jupiter.api.Assertions.*;
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

    private Long id;

    private Message message = Message.builder()
        .content(TEST_NEWS_SUMMARY)
        .publishedAt(TEST_NEWS_PUBLISHED_AT)
        .build();

    @BeforeEach
    public void beforeEach() {
        messageRepository.deleteAll();
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
            .name(SPOT_NAME)
            .description(SPOT_DESCRIPTION)
            .location(location)
            .category(category)
            .build();
        spot = spotRepository.save(spot);

        message = Message.builder()
            .spot(spot)
            .content(TEST_NEWS_TITLE)
            .publishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
        id = messageRepository.save(message).getId();
    }

    @AfterEach
    public void afterEach() {
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void givenUserLoggedIn_whenFindAll_then200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI + "?spot=" + id)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
    }

    @Test
    public void givenNoOneLoggedIn_whenFindAll_then401() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_BASE_URI))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void givenAdminLoggedIn_whenPost_then201() throws Exception {
        MessageDto messageDto = messageMapper.messageToMessageDto(message);
        String body = objectMapper.writeValueAsString(messageDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
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
    public void givenUserLoggedIn_whenPost_then403() throws Exception {
        message.setPublishedAt(null);
        MessageDto messageDto = messageMapper.messageToMessageDto(message);
        String body = objectMapper.writeValueAsString(messageDto);

        MvcResult mvcResult = this.mockMvc.perform(post(MESSAGE_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }
}
