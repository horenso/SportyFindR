package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class LocationDto {

    private Long id;
    private double latitude;
    private double longitude;
}
