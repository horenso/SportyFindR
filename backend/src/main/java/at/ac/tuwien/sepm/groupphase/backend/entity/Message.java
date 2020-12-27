package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
    name = "message-with-spots",
    attributeNodes = {
        @NamedAttributeNode(value = "spot", subgraph = "spot-subgraph")
    },

    subgraphs = {
        @NamedSubgraph(
            name = "spot-subgraph",
            attributeNodes = {
                @NamedAttributeNode("location"),
                @NamedAttributeNode("category")
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
}