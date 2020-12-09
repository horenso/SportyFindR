package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LocationDto {

    private Long id;
    @Max(value = 90, message = "Latitude must not be over 90")
    @Min(value = -90, message = "Latitude must not be under -90")
    private Double latitude;
    @Max(value = 180, message = "longitude must not be over 180")
    @Min(value = -180, message = "longitude must not be under -180")
    private Double longitude;

}
