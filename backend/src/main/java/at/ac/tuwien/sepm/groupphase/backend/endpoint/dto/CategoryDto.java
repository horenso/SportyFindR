package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class CategoryDto {

    private Long id;

    @Size(min = 1, max = 32, message = "Category name should be between 1 and 32 characters long")
    private String name;

    private String icon;
}
