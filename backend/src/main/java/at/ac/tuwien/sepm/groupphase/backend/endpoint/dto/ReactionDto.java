package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class ReactionDto {

    private Long id;
    @NotNull
    private ReactionDtoType type;
    @NotNull
    private Long messageId;

    private UserDto owner;
    public enum ReactionDtoType {
        THUMBS_UP,
        THUMBS_DOWN
    }

}
