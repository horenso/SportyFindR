package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HashtagEndpointTest extends BaseIntegrationTest {

    @Test
    public void getHashtagByName() throws Exception {
        List<Hashtag> hashtagList = List.of(
            hashtagRepository.save(Hashtag.builder().name("wow").build()),
            hashtagRepository.save(Hashtag.builder().name("spot").build()),
            hashtagRepository.save(Hashtag.builder().name("championship").build())
        );

        for (Hashtag hashtag : hashtagList) {
            mockMvc.perform(
                get(HASHTAG_BASE_URI + "/" + hashtag.getName())
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(hashtag.getId()))
                .andExpect(jsonPath("$.name").value(hashtag.getName()));
        }
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
            .downVotes(0)
            .upVotes(0)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message);
        Hashtag hashtag = Hashtag.builder()
            .name("test")
            .messagesList(Collections.singletonList(message))
            .spotsList(Collections.singletonList(spot))
            .build();
        hashtagRepository.save(hashtag);
        Hashtag hashtag2 = Hashtag.builder()
            .name("testomato")
            .messagesList(Collections.singletonList(message))
            .spotsList(Collections.singletonList(spot))
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
}
