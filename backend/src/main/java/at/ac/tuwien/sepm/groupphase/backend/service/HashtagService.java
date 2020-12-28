package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Hashtag;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;

public interface HashtagService {

    Hashtag create(Hashtag hashtag);

    void getHashtags(Message message);

    void deleteMessageInHashtags(Message message);
}
