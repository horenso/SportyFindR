package at.ac.tuwien.sepm.groupphase.backend.entity;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Entity
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    public static final class SpotBuilder {
        private Long id;
        private String name;
        private String description;
        private Location location;
        private Category category;

        private SpotBuilder() {
        }

        public static Spot.SpotBuilder aSpot() {
            return new Spot.SpotBuilder();
        }

        public Spot.SpotBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Spot.SpotBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Spot.SpotBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Spot.SpotBuilder withLocation(Location location) {
            this.location = location;
            return this;
        }

        public Spot.SpotBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public Spot build() {
            Spot spot = new Spot();
            spot.setId(id);
            spot.setName(name);
            spot.setDescription(description);
            spot.setLocation(location);
            spot.setCategory(category);
            return spot;
        }
    }
}
