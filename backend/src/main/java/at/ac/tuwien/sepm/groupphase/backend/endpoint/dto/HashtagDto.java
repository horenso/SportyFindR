package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class HashtagDto {

    private Long id;

    @Size(min = 1, max = 64, message = "Hashtags must be between 1 and 64 characters long")
    private String name;

    private List<MessageDto> messagesList;

    private List<SpotDto> spotsList;
}