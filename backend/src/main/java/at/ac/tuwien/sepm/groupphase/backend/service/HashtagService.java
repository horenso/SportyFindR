package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;

public interface HashtagService {

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
     * Acquires all hashtags from a message
     *
     * @param message to get the hashtags from
     */
    void acquireHashtags(Message message);

    /**
     * Acquires all hashtags from a spot description
     *
     * @param spot to get the hashtags from
     */
    void acquireHashtags(Spot spot);

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
}
