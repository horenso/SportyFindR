package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class RoleDto {

    @NotNull
    private Long id;
    @NotNull
    @Length(min = 3, max = 15)
    private String name;
    private List<Long> userIds;
}
