package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Builder
public class SpotFilter {
    Long locationId;
    String hashtagName;
    Long categoryId;
}
