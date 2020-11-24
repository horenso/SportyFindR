package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class SpotDto {

    private Long id;
    private String name;
    private String description;
    private LocationDto location;

}

