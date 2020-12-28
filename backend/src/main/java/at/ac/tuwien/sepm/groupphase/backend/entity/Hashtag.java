package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;
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

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "hashtags_messages",
        joinColumns = @JoinColumn(name = "hashtag_id"),
        inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private List<Message> messagesList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "hashtags_spots",
        joinColumns = @JoinColumn(name = "hashtag_id"),
        inverseJoinColumns = @JoinColumn(name = "spot_id")
    )
    private List<Spot> spotsList = new ArrayList<>();

    public Hashtag(String name) {
        this.name = name;
    }

    public void addMessage(Message message){
        this.messagesList.add(message);
    }

    public void deleteMessage(Long messageId){
        messagesList.removeIf(message -> message.getId().equals(messageId));
    }

    public void addSpot(Spot spot){
        this.spotsList.add(spot);
    }

}