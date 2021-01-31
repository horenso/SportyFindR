package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.HashtagEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;



@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HashtagEndpointTest implements TestData {
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private HashtagEndpoint hashtagEndpoint;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private MockMvc mockMvc;


    @AfterEach
    public void afterEach(){
        hashtagRepository.deleteAll();
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getHashtagById() {
        Hashtag hashtag = Hashtag.builder()
            .name(HASHTAG_NAME)
            .build();
        hashtagRepository.save(hashtag);
        hashtagEndpoint.getById(hashtag.getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void filterHashtagsByName() throws Exception {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Location location = Location.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        locationRepository.save(location);
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name("owner")
            .password(PASSWORD)
            .build();
        userRepository.save(user);
        Spot spot = Spot.builder()
            .owner(user)
            .name(NAME)
            .location(location)
            .category(category)
            .build();
        spotRepository.save(spot);
        Message message = Message.builder()
            .owner(user)
            .spot(spot)
            .downVotes(ZERO)
            .upVotes(ZERO)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message);
        Hashtag hashtag = Hashtag.builder()
            .name("test")
            .messagesList(Arrays.asList(message))
            .spotsList(Arrays.asList(spot))
            .build();
        hashtagRepository.save(hashtag);
        Hashtag hashtag2 = Hashtag.builder()
            .name("testomato")
            .messagesList(Arrays.asList(message))
            .spotsList(Arrays.asList(spot))
            .build();
        hashtagRepository.save(hashtag2);


        MvcResult mvcResult = this.mockMvc.perform(
            get("/api/v1/hashtags/filter?name=testo")
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("\"id\":"+hashtag2.getId()+",\"name\":\"testomato\""));
    }


    //TODO: continue the test (add messages)
}
