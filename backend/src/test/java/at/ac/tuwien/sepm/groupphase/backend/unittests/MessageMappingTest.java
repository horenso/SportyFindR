package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class MessageMappingTest implements TestData {

    private final Message message = Message.builder()
        .id(1L)
        .content(TEST_NEWS_TEXT)
        .publishedAt(TEST_NEWS_PUBLISHED_AT)
        .build();
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void givenNothing_whenMapDetailedMessageDtoToEntity_thenEntityHasAllProperties() {
        MessageDto messageDto = messageMapper.messageToMessageDto(message);
        assertAll(
            () -> assertEquals(1L, messageDto.getId()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, messageDto.getPublishedAt())
        );
    }
}
