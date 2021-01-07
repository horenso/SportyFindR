package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.hibernate.service.spi.ServiceException;

import java.util.List;


public interface CategoryService {

    /**
     * Create a Category entry
     *
     * @param category to create
     * @return created category entry
     */
    Category create(Category category) throws ServiceException, ValidationException;

    /**
     * Delete a Category entry
     *
     * @param id of category to delete
     */
    void deleteById(Long id) throws NotFoundException2;

    /**
     * Get all existing categories from database.
     *
     * @throws RuntimeException  if something goes wrong during data processing.
     * @throws NotFoundException if no categories could be found in the system.
     */
    List<Category> findAll();
}
