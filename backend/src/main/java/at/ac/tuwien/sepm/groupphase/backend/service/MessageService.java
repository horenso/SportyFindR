package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter.MessageFilter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {

    /**
     * Find all message from one spot ordered by published at date (descending).
     *
     * @param spotId id of the spot
     * @return ordered list of all message entries of this spot
     * @throws NotFoundException if the spot does not exist
     */
    List<Message> findBySpot(Long spotId) throws NotFoundException;

    /**
     * Find all message from one spot ordered by published at date (descending).
     *
     * @param spotId   id of the spot
     * @param pageable containing page information
     * @return ordered list of al message entries of this spot
     * @throws NotFoundException if the spot does not exist
     */
    Page<Message> findBySpotPaged(Long spotId, Pageable pageable) throws NotFoundException;


    /**
     * Create a new message in a spot
     *
     * @param message to be saved
     * @return created message entry
     * @throws NotFoundException   if the spot does not exist
     * @throws ValidationException if the spot is not valid
     */
    Message create(Message message) throws NotFoundException, ValidationException;

    /**
     * Get a Message by its Id
     *
     * @param id the id of the message
     * @return the message that is being looked for
     * @throws NotFoundException if the message does not exist
     */
    Message getById(Long id) throws NotFoundException;

    /**
     * delete a message
     *
     * @param id the id of the message that is being deleted
     * @throws NotFoundException  if the message does not exist
     * @throws WrongUserException if you are not a correct user/do not have the right to delete the message
     * @throws ServiceException   if the message can not be deleted from the HashtagService
     */
    void deleteById(Long id) throws NotFoundException, WrongUserException, ServiceException;

    /**
     * delete a message without being the owner of the message
     *
     * @param id the id of the message that is being deleted
     * @throws NotFoundException if the message does not exist
     * @throws ServiceException  if the message can not be deleted from the HashtagService
     */
    void deleteByIdWithoutAuthentication(Long id) throws NotFoundException, ServiceException;


    /**
     * Finds locations containing spots that match the filter criteria
     *
     * @param messageFilter containing search parameters for message filter
     * @param pageable      containing page information
     * @param messageFilter containing search parameters for message filter
     * @param pageable      containing page information
     * @return Page with messages containing spots that match the filter criteria
     * @throws ServiceException if the Hashtag is invalid
     */
    Page<Message> filter(MessageFilter messageFilter, Pageable pageable);

    /**
     * Returns messages sent by a user
     *
     * @param userId is the id of the user from whom the messages are
     * @return the messages of this user
     * @throws NotFoundException if the user does not exist
     */
    List<Message> findByOwner(Long userId) throws NotFoundException;
}
