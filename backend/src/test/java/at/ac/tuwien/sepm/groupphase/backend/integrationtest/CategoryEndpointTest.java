package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.CategoryEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.SpotEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CategoryEndpointTest implements TestData{
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryEndpoint categoryEndpoint;

    @AfterEach
    public void afterEach() {
        categoryRepository.deleteAll();
    }
    //positive Tests
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllCategories() {
        Category category = Category.builder()
            .name(CAT_NAME)
            .build();
        categoryRepository.save(category);
        Category category2 = Category.builder()
            .name(CAT_NAME2)
            .build();
        categoryRepository.save(category2);
        List<CategoryDto> categoryDtos = categoryEndpoint.getAll();
        assertAll(
            () -> assertEquals(categoryDtos.size(), 2),
            () -> assertEquals(categoryDtos.get(0).getId(), category.getId()),
            () -> assertEquals(categoryDtos.get(0).getName(), category.getName()),
            () -> assertEquals(categoryDtos.get(1).getId(), category2.getId()),
            () -> assertEquals(categoryDtos.get(1).getName(), category2.getName())
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllCategoriesWhenThereAreNoCategories() {
        List<CategoryDto> categoryDtos = categoryEndpoint.getAll();
        assertAll(
            () -> assertEquals(categoryDtos.size(), 0)
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createCategory() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .build();
        categoryDto.setId(categoryEndpoint.create(categoryDto).getId());
        List<Category> categories = categoryRepository.findAll();
        assertAll(
            () -> assertEquals(categories.size(), 1),
            () -> assertEquals(categories.get(0).getId(), categoryDto.getId()),
            () -> assertEquals(categories.get(0).getName(), categoryDto.getName())
        );
    }
    //negative Tests
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createCategoryWithId() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .id(1L)
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> categoryEndpoint.create(categoryDto));
        assertAll(
            () -> assertEquals(0, categoryRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "400 BAD_REQUEST \"Id must be null\"")
        );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void createCategoryWithEmptyName() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name("")
            .build();
        Throwable e = assertThrows(ResponseStatusException.class, () -> categoryEndpoint.create(categoryDto));
        assertAll(
            () -> assertEquals(0, categoryRepository.findAll().size()),
            () -> assertEquals(e.getMessage(), "400 BAD_REQUEST \"Category must have a name\"")
        );
    }
}
