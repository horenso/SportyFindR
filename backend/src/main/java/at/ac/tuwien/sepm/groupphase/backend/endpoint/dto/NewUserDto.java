package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode
public class NewUserDto extends UserDto {

    @NotNull
    @Size(min = 7, message = "Password must be at least 7 characters long")
    private String password;
}
