package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;

import java.util.List;

public interface MessageService {

    /**
     * Find all message from one spot ordered by published at date (descending).
     *
     * @param spotId id of the spot
     * @return ordered list of al message entries
     */
    List<Message> findBySpot(Long spotId);


    /**
     * Create a new message in a spot
     *
     * @param message to be saved
     * @return created message entry
     */
    Message create(Message message);

    Message getById(Long id) throws ServiceException;
}
