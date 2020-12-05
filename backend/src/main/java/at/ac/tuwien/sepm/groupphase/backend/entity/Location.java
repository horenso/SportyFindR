package at.ac.tuwien.sepm.groupphase.backend.entity;
import lombok.*;
import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private Double latitude;

    @Column(nullable = false, length = 10)
    private Double longitude;


    public static final class LocationBuilder {
        private Long id;
        private Double latitude;
        private Double longitude;

        private LocationBuilder() {
        }

        public static Location.LocationBuilder aLocation() {
            return new Location.LocationBuilder();
        }

        public Location.LocationBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Location.LocationBuilder withLatitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Location.LocationBuilder withLongitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }
        public Location build() {
            Location location = new Location();
            location.setId(id);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            return location;
        }
    }
}
