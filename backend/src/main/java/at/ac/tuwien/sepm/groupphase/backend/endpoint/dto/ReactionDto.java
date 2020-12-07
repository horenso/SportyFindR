package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ReactionDto {

    public enum ReactionDtoType {
        THUMBS_UP,
        THUMBS_DOWN
    }

    private Long id;
    private ReactionDtoType reactionType;
    private Long messageId;

}
