package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Builder
public class LocationFilter {
    Long categoryId;
    Double latitude;
    Double longitude;
    Double radius;
    String hashtag;
}
