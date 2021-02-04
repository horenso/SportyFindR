package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter.SpotFilter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;

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
    Spot update(Spot spot) throws NotFoundException, ValidationException, WrongUserException;


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
     * Get all spots with certain criteria.
     *
     * @param spotFilter with the location id, the hashtag name and the category id
     * @return list of spots
     * @throws ValidationException if a location id is specified but the location doesn't exist, same with the category
     */
    List<Spot> findSpots(SpotFilter spotFilter) throws ValidationException;

    /**
     * Get one spot by spotId
     *
     * @param spotId of the spot
     * @return the spot entity
     * @throws NotFoundException if the spot was not found
     */
    Spot getOneById(Long spotId) throws NotFoundException;

    /**
     * Find all Spots by User ID of their owner
     *
     * @param userId of the owner
     * @return list of spots
     * @throws NotFoundException if the user id cannot be found
     */
    List<Spot> findSpotsByUserId(Long userId) throws NotFoundException;
}
