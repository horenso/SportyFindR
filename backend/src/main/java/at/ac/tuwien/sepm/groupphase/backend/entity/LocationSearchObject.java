package at.ac.tuwien.sepm.groupphase.backend.entity;

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

}
