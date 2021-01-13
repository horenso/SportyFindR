package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class SpotDto {


    private Long id;

    @NotBlank(message = "The spot's name can't be blank")
    @NotNull(message = "The spot must have a name")
    private String name;

    @Size(max = 500, message = "description can't be longer than 500 chars.")
    private String description;

    @NotNull(message = "The spot must have a location")
    @Valid
    private LocationDto location;

    @NotNull(message = "The spot must have a category")
    @Valid
    private CategoryDto category;

    private UserDto owner;
}

