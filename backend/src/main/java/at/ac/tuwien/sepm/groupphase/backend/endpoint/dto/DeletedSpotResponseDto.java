package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class DeletedSpotResponseDto {
    boolean deletedLocation;
}
