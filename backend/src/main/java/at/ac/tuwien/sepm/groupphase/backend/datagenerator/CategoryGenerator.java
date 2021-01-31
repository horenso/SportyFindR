package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Profile("generateCat")
@Component
public class CategoryGenerator {

    // Parameters
    private static final HashMap<String, String> CATEGORIES = createMap();

    private final CategoryService categoryService;

    public CategoryGenerator(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private static HashMap<String, String> createMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Baseball", "sports_baseball");
        map.put("Basketball", "sports_basketball");
        map.put("Cricket", "sports_cricket");
        map.put("Football", "sports_football");
        map.put("Golf", "sports_golf");
        map.put("Handball", "sports_handball");
        map.put("Hiking", "hiking");
        map.put("Hockey", "sports_hockey");
        map.put("Ice Skating", "ice_skating");
        map.put("Kabaddi", "sports_kabaddi");
        map.put("Kitesurfing", "kitesurfing");
        map.put("Meditation", "self_improvement");
        map.put("Motorsports", "sports_motorsports");
        map.put("Nordic Walking", "nordic_walking");
        map.put("Rugby", "sports_rugby");
        map.put("Soccer", "sports_soccer");
        map.put("Skateboarding", "skateboarding");
        map.put("Skiing", "downhill_skiing");
        map.put("Tennis", "sports_tennis");
        map.put("Volleyball", "sports_volleyball");
        return map;
    }

    @PostConstruct
    private void generateData() throws ValidationException {
        try {
            for (Map.Entry<String, String> mapElement : CATEGORIES.entrySet()) {
                String key = mapElement.getKey();
                String value = mapElement.getValue();
                Category cat = Category.builder()
                    .name(key)
                    .icon(value)
                    .build();
                categoryService.create(cat);
            }
        } catch (ValidationException e) {
            throw new ValidationException("Couldn't create Category", e);
        }
    }
}
