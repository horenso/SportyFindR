package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
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

    @NotNull(message = "Content must not be null")
    private String content;

    @NotNull(message = "SpotId must not be null")
    private Long spotId;

    @Null(message = "UpVotes must be null")
    private Integer upVotes;

    @Null(message = "Down Votes must be null")
    private Integer downVotes;

    private SimpleUserDto owner;
}