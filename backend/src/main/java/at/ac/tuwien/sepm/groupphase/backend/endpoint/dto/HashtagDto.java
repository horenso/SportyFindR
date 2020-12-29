package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;
import lombok.*;
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
    private String name;
    private List<MessageDto> messagesList = new ArrayList<>();
    //private List<SpotDto> spotsList = new ArrayList<>();
}