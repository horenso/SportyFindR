package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface SpotService {

    /**
     * Create a Spot entry
     *
     * @param spot to create
     * @return created spot entry
     */
    Spot create(Spot spot) throws ServiceException, ValidationException;

    /**
     * Update a Spot entry
     *
     * @param spot to be updated, id determines what spot gets updated
     * @return spot as it is saved in the system
     * @throws ServiceException if the spot entity is not valid
     */
    Spot update(Spot spot) throws ServiceException;


    /**
     * Deletes one spot by id, all messages and reactions associated with that spot get
     * deleted as well.
     *
     * @param id of the spot that should be deleted
     * @throws ValidationException if no spot with this id is known
     */
    void deleteById(Long id) throws ValidationException;

    /**
     * Get all spots within one Location. This list cannot be empty since since a location
     * must contain at least one spot.
     *
     * @param locationId of the location containing the spots
     * @return list of spots
     */
    List<Spot> getSpotsByLocation(Long locationId);

    /**
     * Get one spot by spotId
     *
     * @param spotId of the spot
     * @throws NotFoundException if the spot was not found
     * @return the spot entity
     */
    Spot getOneById(Long spotId);
}
