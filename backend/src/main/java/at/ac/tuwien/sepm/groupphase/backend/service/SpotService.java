package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;

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
    Spot update(Spot spot) throws NotFoundException2, ValidationException, WrongUserException;


    /**
     * Deletes one spot by id, all messages and reactions associated with that spot get
     * deleted as well.
     *
     * @param id of the spot that should be deleted
     * @return true if the host location was deleted, otherwise false
     * @throws ValidationException if no spot with this id is known
     */
    boolean deleteById(Long id) throws ValidationException, ServiceException, WrongUserException;

    /**
     * Get all spots within one Location. This list cannot be empty since since a location
     * must contain at least one spot.
     *
     * @param locationId of the location containing the spots
     * @return list of spots
     * @throws ValidationException
     */
    List<Spot> getSpotsByLocation(Long locationId, String hashtagName) throws ValidationException;

    /**
     * Get one spot by spotId
     *
     * @param spotId of the spot
     * @return the spot entity
     * @throws NotFoundException if the spot was not found
     */
    Spot getOneById(Long spotId) throws NotFoundException2;

    /**
     * Find all Spots by User ID of their owner
     * @param userId of the owner
     * @return list of spots
     * @throws NotFoundException2 if the user id cannot be found
     */
    List<Spot> findSpotsByUserId(Long userId) throws NotFoundException2;
}
