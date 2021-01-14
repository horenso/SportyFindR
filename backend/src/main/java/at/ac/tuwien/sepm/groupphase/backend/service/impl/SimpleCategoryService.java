package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SimpleCategoryService implements CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CategoryRepository categoryRepository;

    public SimpleCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(Category category) throws ServiceException, ValidationException {
        LOGGER.debug("Create new Category {}", category);

        if (category.getId() != null) {
            throw new ValidationException("Id must be null");
        }
        if (category.getName() == null || category.getName().equals("")) {
            throw new ValidationException("Category must have a name");
        }

        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) throws NotFoundException2 {
        LOGGER.debug("Delete Category with id {}", id);
        if (categoryRepository.findById(id).isEmpty()) {
            throw new NotFoundException2("There is no category with id " + id);
        }
        categoryRepository.deleteById(id);

    }

    @Override
    public List<Category> findAll() {
        LOGGER.debug("Get all categories.");
        return categoryRepository.findAll();
    }
}
