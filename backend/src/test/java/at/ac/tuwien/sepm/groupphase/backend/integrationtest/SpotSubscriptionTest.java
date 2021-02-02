package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReactionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.AsyncListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureMockMvc
@Slf4j
public class SpotSubscriptionTest extends BaseIntegrationTest {

    @Test
    public void getCorrectNewMessageEvents() throws Exception {
        MvcResult result = mockMvc.perform(
            get(SUBSCRIPTION_URI)
                .param("spotId", spot1.getId().toString()))
            .andReturn();

        MessageDto firstMessageSpot1 = MessageDto.builder()
            .content("Hi Spot 1")
            .spotId(spot1.getId()).build();

        MessageDto messageSpot2 = MessageDto.builder()
            .content("Hi Spot 2")
            .spotId(spot2.getId()).build();

        MessageDto secondMessageSpot1 = MessageDto.builder()
            .content("Hi again Spot 1")
            .spotId(spot1.getId()).build();

        mockMvc.perform(post(MESSAGE_BASE_URI + "/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(firstMessageSpot1)))
            .andExpect(status().isCreated());

        mockMvc.perform(post(MESSAGE_BASE_URI + "/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(messageSpot2)))
            .andExpect(status().isCreated());

        mockMvc.perform(post(MESSAGE_BASE_URI + "/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(secondMessageSpot1)))
            .andExpect(status().isCreated());

        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }

        mockMvc.perform(asyncDispatch(result))
            .andExpect(content().string(containsString("Hi Spot 1")))
            .andExpect(content().string(containsString("Hi again Spot 1")))
            .andExpect(content().string(allOf(not(containsString("Hi Spot 2")))));
    }

    @Test
    public void getCorrectDeleteEvents() throws Exception {
        List<String> messagesSpot1Ids = new ArrayList<>();
        List<String> messagesSpot2Ids = new ArrayList<>();

        // Ten Messages each in spot1 and spot2
        for (int i = 0; i < 10; i++) {
            var r1 = mockMvc.perform(post(MESSAGE_BASE_URI + "/")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    MessageDto.builder().content("Bla").spotId(spot1.getId()).build())))
                .andExpect(status().isCreated())
                .andReturn();
            var r2 = mockMvc.perform(post(MESSAGE_BASE_URI + "/")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    MessageDto.builder().content("Bla").spotId(spot2.getId()).build())))
                .andExpect(status().isCreated())
                .andReturn();
            messagesSpot1Ids.add(
                objectMapper.readValue(r1.getResponse().getContentAsString(), MessageDto.class).getId().toString());
            messagesSpot2Ids.add(
                objectMapper.readValue(r2.getResponse().getContentAsString(), MessageDto.class).getId().toString());
        }

        assertEquals(20, messageRepository.count());
        assertEquals(10, messageRepository.findAllBySpot_Id(spot1.getId()).size());

        MvcResult result = mockMvc.perform(
            get(SUBSCRIPTION_URI)
                .param("spotId", spot1.getId().toString()))
            .andReturn();

        // Delete all spots
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(delete(MESSAGE_BASE_URI + "/" + messagesSpot1Ids.get(i))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
                .andExpect(status().isOk());
            mockMvc.perform(delete(MESSAGE_BASE_URI + "/" + messagesSpot2Ids.get(i))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
                .andExpect(status().isOk());
        }

        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }

        var events = mockMvc.perform(asyncDispatch(result)).andReturn().getResponse().getContentAsString();

        for (String id : messagesSpot1Ids) {
            assertTrue(events.contains("{\"id\":" + id));
        }
        for (String id : messagesSpot2Ids) {
            assertFalse(events.contains("{\"id\":" + id));
        }
    }

    @Test
    public void getCorrectUpdatedReactionsEvents() throws Exception {
        Message message1 = messageRepository.save(
            Message.builder().content("Message 1").owner(user1).spot(spot1).publishedAt(LocalDateTime.now()).build());
        Message message2 = messageRepository.save(
            Message.builder().content("Message 2").owner(user1).spot(spot1).publishedAt(LocalDateTime.now()).build());

        MvcResult result = mockMvc.perform(
            get(SUBSCRIPTION_URI)
                .param("spotId", spot1.getId().toString()))
            .andReturn();

        mockMvc.perform(post(REACTIONS_BASE_URI + "/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                ReactionDto.builder()
                    .messageId(message1.getId())
                    .type(ReactionDto.ReactionDtoType.THUMBS_UP).build())))
            .andExpect(status().isCreated());

        mockMvc.perform(patch(REACTIONS_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                ReactionDto.builder()
                    .id(1L)
                    .messageId(message1.getId())
                    .type(ReactionDto.ReactionDtoType.THUMBS_DOWN).build())))
            .andExpect(status().isOk());

        mockMvc.perform(post(REACTIONS_BASE_URI + "/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user2.getEmail(), USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                ReactionDto.builder()
                    .messageId(message1.getId())
                    .type(ReactionDto.ReactionDtoType.THUMBS_UP).build())))
            .andExpect(status().isCreated())
            .andDo(print());

        mockMvc.perform(delete(REACTIONS_BASE_URI + "/" + 2L)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user2.getEmail(), USER_ROLES)))
            .andExpect(status().isOk());

        mockMvc.perform(delete(REACTIONS_BASE_URI + "/" + 1L)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
            .andExpect(status().isOk());

        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }

        var events = mockMvc.perform(asyncDispatch(result)).andReturn().getResponse().getContentAsString();

        List<String> expectations = List.of(
            "\"upVotes\":1,\"downVotes\":0",
            "\"upVotes\":0,\"downVotes\":1",
            "\"upVotes\":1,\"downVotes\":1",
            "\"upVotes\":0,\"downVotes\":1",
            "\"upVotes\":0,\"downVotes\":0"
        );

        List<String> lines = events.lines().collect(Collectors.toList());

        int expectation = 0;
        for (String line : lines) {
            if (!line.startsWith("data")) {
                continue;
            }
            assertTrue(line.contains(expectations.get(expectation)));
            expectation++;
        }
    }

    @AfterEach
    public void afterEach() {
        hashtagRepository.deleteAll();
        reactionRepository.deleteAll();
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
