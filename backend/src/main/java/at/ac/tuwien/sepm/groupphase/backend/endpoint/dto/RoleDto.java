package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

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
    private String name;
    private List<UserDto> users;
}
