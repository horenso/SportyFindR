package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Entity
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Length(min = 1, max = 64)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
        name = "hashtags_messages",
        joinColumns = @JoinColumn(name = "hashtag_id"),
        inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private List<Message> messagesList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
        name = "hashtags_spots",
        joinColumns = @JoinColumn(name = "hashtag_id"),
        inverseJoinColumns = @JoinColumn(name = "spot_id")
    )
    private List<Spot> spotsList = new ArrayList<>();

    public Hashtag(String name) {
        this.name = name;
    }

    public void addMessage(Message message) {
        this.messagesList.add(message);
    }

    public void deleteMessage(Long messageId) {
        messagesList.removeIf(message -> message.getId().equals(messageId));
    }

    public void addSpot(Spot spot) {
        this.spotsList.add(spot);
    }

    public void deleteSpot(Long spotId) {
        spotsList.removeIf(spot -> spot.getId().equals(spotId));
    }

}