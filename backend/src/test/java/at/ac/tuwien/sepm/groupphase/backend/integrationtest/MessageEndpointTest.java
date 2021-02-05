package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration
@AutoConfigureMockMvc
public class MessageEndpointTest extends BaseIntegrationTest {

    @Test
    public void findBySpot_nonexistentSpotId() throws Exception {
        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", "200")
                .queryParam("size", "26")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
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
                .spot(spot1)
                .publishedAt(LocalDateTime.now()).build();
            messageList.add(messageRepository.save(message));
        });

        // Get all Messages
        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", spot1.getId().toString())
                .queryParam("size", "26")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(26)))
            .andExpect(jsonPath("$.numberOfElements").value(26))
            .andExpect(jsonPath("$.last").value(true));

        // Get the first 5 Messages
        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", spot1.getId().toString())
                .queryParam("size", "5")
                .queryParam("page", "0")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)))
            .andExpect(jsonPath("$.numberOfElements").value(5))
            .andExpect(jsonPath("$.last").value(false))
            .andExpect(jsonPath("$.content[0].content").value("Z"))
            .andExpect(jsonPath("$.content[1].content").value("Y"))
            .andExpect(jsonPath("$.content[4].content").value("V"));

        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", spot1.getId().toString())
                .queryParam("size", "5")
                .queryParam("page", "1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(5)))
            .andExpect(jsonPath("$.numberOfElements").value(5))
            .andExpect(jsonPath("$.last").value(false))
            .andExpect(jsonPath("$.content[0].content").value("U"))
            .andExpect(jsonPath("$.content[4].content").value("Q"));

        mockMvc
            .perform(get(MESSAGE_BASE_URI)
                .param("spotId", spot1.getId().toString())
                .queryParam("size", "5")
                .queryParam("page", "5")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(jsonPath("$.last").value(true))
            .andExpect(jsonPath("$.content[0].content").value("A"))
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
    public void create_basicPositiveTests() throws Exception {
        int messageCount = 100;
        List<MessageDto> messageDtoList = new ArrayList<>();

        for (int i = 0; i < messageCount; i++) {
            messageDtoList.add(MessageDto.builder()
                .content("Message 1")
                .spotId(spot1.getId()).build());
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
                .param("spotId", spot1.getId().toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(messageCount));
    }

    @Test
    public void create_messageMustNotBeOnlyWhitespaces() throws Exception {
        List<MessageDto> onlyWhitespace = List.of(
            MessageDto.builder().content("       ").build(),
            MessageDto.builder().content(" ").build(),
            MessageDto.builder().content("\t\t").build(),
            MessageDto.builder().content("\n").build(),
            MessageDto.builder().content("\r").build());

        for (MessageDto messageDto : onlyWhitespace) {
            mockMvc.perform(post(MESSAGE_BASE_URI + "/")
                .param("spotId", spot1.getId().toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void create_messageMustNotExpireInThePast() throws Exception {
        MessageDto messageDto = MessageDto.builder()
            .content(MESSAGE_CONTENT)
            .expirationDate(LocalDateTime.now().minusMinutes(1L))
            .build();

        mockMvc.perform(post(MESSAGE_BASE_URI + "/")
            .param("spotId", spot1.getId().toString())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user1.getEmail(), USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(messageDto)))
            .andExpect(status().isBadRequest());

        assertEquals(0, this.messageRepository.findAll().size());
    }

    @Test
    public void getById_nonexistentId() throws Exception {
        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/1")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getById_basicPositive() throws Exception {
        Message message = Message.builder()
            .owner(user1)
            .content(MESSAGE_CONTENT)
            .spot(spot1)
            .publishedAt(LocalDateTime.now()).build();
        message = messageRepository.save(message);

        mockMvc
            .perform(get(MESSAGE_BASE_URI + "/" + message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(EMAIL, USER_ROLES)))
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
            .spotId(spot1.getId()).build();

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

        assertEquals(1, messageRepository.findAll().size());
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
            .spot(spot1)
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
            .spot(spot1)
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
            .spot(spot1)
            .publishedAt(LocalDateTime.now()).build();
        message = messageRepository.save(message);

        mockMvc
            .perform(delete(MESSAGE_BASE_URI + "/" + message.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(admin.getEmail(), ADMIN_ROLES)))
            .andExpect(status().isOk());

        assertTrue(messageRepository.findById(message.getId()).isEmpty());
    }

    @Test
    public void filterAllMessagesWithCategoryHashtagUserTime() throws Exception {
        Message message = Message.builder()
            .owner(user1)
            .spot(spot1)
            .downVotes(0)
            .upVotes(0)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message);
        Message message2 = Message.builder()
            .owner(user1)
            .spot(spot1)
            .downVotes(0)
            .upVotes(0)
            .content(MESSAGE_CONTENT)
            .publishedAt(DATE)
            .build();
        messageRepository.save(message2);
        Hashtag hashtag = Hashtag.builder()
            .name("test")
            .messagesList(Collections.singletonList(message))
            .spotsList(Collections.singletonList(spot1))
            .build();
        hashtagRepository.save(hashtag);

        MvcResult mvcResult = this.mockMvc.perform(get(MESSAGE_FILTER_URI)
            .queryParam("categoryId", category.getId().toString())
            .queryParam("hashtag", hashtag.getName())
            .queryParam("user", user1.getName())
            .queryParam("time", "1000-01-01")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andReturn();
    }
}
