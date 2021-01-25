package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class UserDto {

    private Long id;
    @NotNull
    @Size(min = 3, max = 30, message = "Shortest user name: 3 characters, longest user name: 30 characters")
    private String name;
    @NotNull
    @Size(min = 6, max = 30, message = "Please provide a valid email address with 30 characters at most")
    private String email;
    @Size(min = 7, message = "Password must be at least 7 characters long")
    private String password;
    private Boolean enabled;
    @Singular
    private List<Long> roleIds;
}
