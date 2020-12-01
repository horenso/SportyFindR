package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {
    Category categoryDtoToCategory(CategoryDto category);
    CategoryDto categoryToCategoryDto(Category category);
}
