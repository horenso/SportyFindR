package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.MessageSearchObject;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
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
     * Create a new message in a spot
     *
     * @param message to be saved
     * @return created message entry
     */
    Message create(Message message) throws NotFoundException2;

    Message getById(Long id) throws NotFoundException2;

    void deleteById(Long id) throws NotFoundException2;

    /**
     * Finds locations containing spots that match the filter criteria
     *
     * @param messageSearchObject containing search parameters for message filter
     * @param pageable containing page information
     * @return Page with messages containing spots that match the filter criteria
     */
    Page<Message> filter(MessageSearchObject messageSearchObject, Pageable pageable) throws NotFoundException, ServiceException;
}
