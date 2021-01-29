package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

import java.util.List;

public interface HashtagService {

    /**
     * Gets a Hashtag by its ID
     *
     * @param id of hashtag to find
     * @return the hashtag corresponding to the id
     */
    Hashtag getOneById(Long id);

    /**
     * Gets a Hashtag by its name
     *
     * @param name of hashtag to find
     * @return the hashtag corresponding to that name
     */
    Hashtag getByName(String name);

    /**
     * Create a Hashtag entry
     *
     * @param hashtag to create
     * @return created hashtag entry
     */
    Hashtag create(Hashtag hashtag);

    /**
     * Get all hashtags from a message
     *
     * @param message to get the hashtags from
     */
    void getHashtags(Message message);

    /**
     * Get all hashtags from a spot description
     *
     * @param spot to get the hashtags from
     */
    void getHashtags(Spot spot);

    /**
     * Remove a message from the hashtags
     *
     * @param message to be removed
     */
    void deleteMessageInHashtags(Message message);

    /**
     * Remove a spot from the hashtags
     *
     * @param spot to be removed
     */
    void deleteSpotInHashtags(Spot spot);

    /**
     * Get all existing hashtags from database.
     *
     * @throws RuntimeException  if something goes wrong during data processing.
     * @throws NotFoundException if no hashtags could be found in the system.
     */
    List<Hashtag> findAll();
}
