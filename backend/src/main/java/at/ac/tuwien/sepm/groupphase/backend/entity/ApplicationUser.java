package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
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
@Table(name = "applicationusers")
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Length(min = 3, max = 30)
    private String name;

    @Column(nullable = false, unique = true)
    @Length(min = 6, max = 30)
    private String email;

    @Column(nullable = false)
    @Length(min = 7)
    private String password;

    private Boolean enabled = false;

    @Singular
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
        name = "applicationusers_roles",
        joinColumns = { @JoinColumn(name = "applicationuser_id") },
        inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private Set<Role> roles = new HashSet<>();

/*
    public ApplicationUser(Long id, @Length(min = 3, max = 30) String name, @Length(min = 6, max = 30) String email, @Length(min = 7) String password, Boolean enabled, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.roles = Objects.requireNonNullElseGet(roles, HashSet::new);
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationUser user = (ApplicationUser) o;
        return getId().equals(user.getId()) && getName().equals(user.getName()) && getEmail().equals(user.getEmail()) && getEnabled().equals(user.getEnabled()) && Objects.equals(getRoles(), user.getRoles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail(), getEnabled(), getRoles());
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", enabled=" + enabled +
            ", roles=" + roles +
            '}';
    }
}
