package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.WrongUserException;
import org.springframework.security.core.Authentication;

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

    void deleteById(Long id) throws NotFoundException2, WrongUserException;
    void deleteByIdWithoutAuthentication(Long id) throws NotFoundException2, WrongUserException;


    /**
     * Finds locations containing spots that match the filter criteria
     *
     * @param categoryId of spot
     * @param latitude   of the current location of the user
     * @param longitude  of the current location of the user
     * @param radius     determining the maximum distance of filtered locations from user
     * @param time       of sent messages
     * @return List of messages containing spots that match the filter criteria
     */
    List<Message> filter(Long categoryId, Double latitude, Double longitude, Double radius, LocalDateTime time) throws NotFoundException, ServiceException;
}
