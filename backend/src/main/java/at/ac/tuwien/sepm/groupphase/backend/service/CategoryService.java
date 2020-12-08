package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.hibernate.service.spi.ServiceException;


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
    void deleteById(Long id) throws NotFoundException;
}
