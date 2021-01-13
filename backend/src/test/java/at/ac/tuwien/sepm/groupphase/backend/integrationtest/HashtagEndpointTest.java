package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.HashtagEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.repository.HashtagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HashtagEndpointTest implements TestData {
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private HashtagEndpoint hashtagEndpoint;

    @AfterEach
    public void afterEach(){
        hashtagRepository.deleteAll();
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
    //TODO: continue the test (add messages)
}
