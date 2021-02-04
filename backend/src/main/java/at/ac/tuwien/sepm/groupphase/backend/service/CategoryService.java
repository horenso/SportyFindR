package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;


public interface CategoryService {

    /**
     * Create a Category entry
     *
     * @param category to create
     * @return created category entry
     * @throws ValidationException is thrown when the category is not valid
     */
    Category create(Category category) throws ValidationException;

    /**
     * Delete a Category entry
     *
     * @param id of category to delete
     * @throws NotFoundException2 is throw when the category that should be deleted does not exist
     */
    void deleteById(Long id) throws NotFoundException2;

    /**
     * Get all existing categories from database.
     *
     * @return all categories
     */
    List<Category> findAll();
}
