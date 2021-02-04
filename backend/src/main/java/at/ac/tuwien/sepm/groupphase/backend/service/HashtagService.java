package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface HashtagService {


    /**
     * Gets a Hashtag by its name
     *
     * @param name of hashtag to find
     * @return the hashtag corresponding to that name
     * @throws ValidationException if the name is empty
     */
    Hashtag getByName(String name) throws ValidationException;

    /**
     * Gets a Hashtag list by its name
     *
     * @param name of hashtag to find
     * @return the hashtag corresponding to that name
     */
    List<Hashtag> searchByName(String name);


    /**
     * Acquires all hashtags from a message and saves them if they dont already exist.
     * * It also saves the connection between the message and the hashtag
     *
     * @param message to get the hashtags from
     * @throws ValidationException if the message content is empty
     */
    void acquireHashtags(Message message) throws ValidationException;

    /**
     * Takes all hashtags from a spot description and saves them if they dont already exist.
     * It also saves the connection between the spot and the hashtag
     *
     * @param spot to get the hashtags from
     * @throws ValidationException if the spot description is empty
     */
    void acquireHashtags(Spot spot) throws ValidationException;

    /**
     * Remove a message from the hashtags
     *
     * @param message to be removed
     * @throws ValidationException if the message content is empty
     */
    void deleteMessageInHashtags(Message message) throws ValidationException;

    /**
     * Remove a spot from the hashtags
     *
     * @param spot to be removed
     * @throws ValidationException if the spot description is empty
     */
    void deleteSpotInHashtags(Spot spot) throws ValidationException;

    /**
     * Get all existing hashtags from database.
     *
     * @return all Hashtags
     */
    List<Hashtag> findAll();
}
