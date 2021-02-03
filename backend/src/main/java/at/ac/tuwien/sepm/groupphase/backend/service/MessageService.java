package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter.MessageFilter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
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
     * @return ordered list of al message entries
     */
    List<Message> findBySpot(Long spotId) throws NotFoundException2;

    /**
     * Find all message from one spot ordered by published at date (descending).
     *
     * @param spotId id of the spot
     * @param pageable containing page information
     * @return ordered list of al message entries
     */
    Page<Message> findBySpotPaged(Long spotId, Pageable pageable) throws NotFoundException2;


    /**
     * Create a new message in a spot
     *
     * @param message to be saved
     * @return created message entry
     */
    Message create(Message message) throws NotFoundException2, ValidationException;

    Message getById(Long id) throws NotFoundException2;

    void deleteById(Long id) throws NotFoundException2, WrongUserException;
    void deleteByIdWithoutAuthentication(Long id) throws NotFoundException2, WrongUserException;


    /**
     * Finds locations containing spots that match the filter criteria
     *
     * @param messageFilter containing search parameters for message filter
     * @param pageable      containing page information
     * @return Page with messages containing spots that match the filter criteria
     */
    Page<Message> filter(MessageFilter messageFilter, Pageable pageable);

    List<Message> findByOwner(Long userId) throws NotFoundException2;
}
