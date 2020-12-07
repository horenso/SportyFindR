package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import org.hibernate.service.spi.ServiceException;

import javax.xml.bind.ValidationException;

public interface SpotService {

    /**
     * Create a Spot entry
     *
     * @param spot to create
     * @return created spot entry
     */
    Spot create(Spot spot) throws ServiceException, ValidationException;

    void deleteById(Long id) throws ValidationException;
}
