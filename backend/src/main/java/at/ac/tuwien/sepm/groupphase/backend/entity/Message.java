package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
    name = "message-with-spots-and-owner",
    attributeNodes = {
        @NamedAttributeNode(value = "spot", subgraph = "spot-subgraph"),
        @NamedAttributeNode(value = "owner", subgraph = "owner-subgraph")
    },

    subgraphs = {
        @NamedSubgraph(
            name = "spot-subgraph",
            attributeNodes = {
                @NamedAttributeNode("location"),
                @NamedAttributeNode("category")
            }
        ),
        @NamedSubgraph(
            name = "owner-subgraph",
            attributeNodes = {
                @NamedAttributeNode("id"),
                @NamedAttributeNode("name"),
                @NamedAttributeNode("email"),
                @NamedAttributeNode("password"),
                @NamedAttributeNode("enabled"),
                @NamedAttributeNode("roles")
            }
        )
    }
)

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Length(min = 1, max = 500)
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spot spot;

    @Transient
    private Integer upVotes;

    @Transient
    private Integer downVotes;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private ApplicationUser owner;

    private LocalDateTime expirationDate;

    @Transient
    private Reaction.ReactionType ownerReaction;

    @Transient
    private Long ownerReactionId;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
        name = "hashtags_messages",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtagList = new ArrayList<>();
}