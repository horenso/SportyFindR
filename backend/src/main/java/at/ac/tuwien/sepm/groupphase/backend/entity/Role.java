package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 15)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<ApplicationUser> applicationUsers = new HashSet<>();

    public Role(Long id, @Length(min = 3, max = 15) String name, Set<ApplicationUser> applicationUsers) {
        this.id = id;
        this.name = name;
        this.applicationUsers = Objects.requireNonNullElseGet(applicationUsers, HashSet::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return getName().equals(role.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "Role{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
