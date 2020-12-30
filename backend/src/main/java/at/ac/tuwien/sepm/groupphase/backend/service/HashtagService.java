package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;

public interface HashtagService {

    Hashtag getByName(String name);

    Hashtag create(Hashtag hashtag);

    void getHashtags(Message message);

    void getHashtags(Spot spot);

    void deleteMessageInHashtags(Message message);

    void deleteSpotInHashtags(Spot spot);
}
