package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReactionService {

    /**
     * Creates a Reaction Entity
     * @param reaction to be stored
     * @return created Reaction Entity
     */
    Reaction create(Reaction reaction);

}
