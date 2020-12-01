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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    public static final class CategoryBuilder {
        private Long id;
        private String name;

        private CategoryBuilder() {
        }

        public static Category.CategoryBuilder aCategory() {
            return new Category.CategoryBuilder();
        }

        public Category.CategoryBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Category.CategoryBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Category build() {
            Category category = new Category();
            category.setId(id);
            category.setName(name);
            return category;
        }
    }

}
