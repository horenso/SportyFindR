package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class SpotDto {

    private Long id;
    @NotNull(message = "The spot must have a name")
    private String name;
    private String description;
    @NotNull(message = "The spot must have a location")
    @Valid
    private LocationDto location;
    @NotNull(message = "The spot must have a category")
    private CategoryDto category;

}

