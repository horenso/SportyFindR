package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class LocationSearchObject {
    Long categoryId;
    Double latitude;
    Double longitude;
    Double radius;
    String hashtag;
}
