package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class MessageFilter {

    Long categoryId;
    String hashtagName;
    String user;
    LocalDateTime time;
}
