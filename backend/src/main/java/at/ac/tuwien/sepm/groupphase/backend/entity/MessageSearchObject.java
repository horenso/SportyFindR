package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class MessageSearchObject {

    Long categoryId;
    Long hashtagId;
    LocalDateTime time;
}
