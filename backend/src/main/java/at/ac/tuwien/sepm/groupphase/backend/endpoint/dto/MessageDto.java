package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
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
    @Size(min = 1, max = 500, message = "Content must be between 1 and 512 characters long")
    private String content;

    @NotNull(message = "SpotId must not be null")
    private Long spotId;

    @Null(message = "UpVotes must be null")
    private Integer upVotes;

    @Null(message = "Down Votes must be null")
    private Integer downVotes;
}