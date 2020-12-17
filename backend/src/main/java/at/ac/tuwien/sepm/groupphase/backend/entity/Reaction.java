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

public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) // TODO: Do we need this?
    private LocalDateTime publishedAt;
    @Column(nullable = false)
    private ReactionType type;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    public enum ReactionType {
        THUMBS_UP,
        THUMBS_DOWN
    }

    //TODO Add userID once users are implemented
}
