package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 15)
    private String name;

    @Singular
    @ManyToMany(
        targetEntity = ApplicationUser.class,
//        mappedBy = "roles",
        cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
//            CascadeType.PERSIST,
//            CascadeType.MERGE,
        },
        fetch = FetchType.EAGER
    )
    @JoinTable(
        name = "applicationusers_roles",
        joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "applicationuser_id", referencedColumnName = "id")}
    )
//    @Fetch(value = FetchMode.JOIN)
    private Set<ApplicationUser> applicationUsers = new HashSet<>();

/*
    public Role(Long id, @Length(min = 3, max = 15) String name, Set<ApplicationUser> applicationUsers) {
        this.id = id;
        this.name = name;
        this.applicationUsers = Objects.requireNonNullElseGet(applicationUsers, HashSet::new);
    }

*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return getId().equals(role.getId()) && getName().equals(role.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Role{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
