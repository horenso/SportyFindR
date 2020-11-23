package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class MessageDto {

    private Long id;
    private LocalDateTime publishedAt;
    private String content;
}