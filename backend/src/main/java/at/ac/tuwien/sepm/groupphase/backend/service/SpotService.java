package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;

public interface SpotService {

    /**
     * Create a Spot entry
     *
     * @param spot to create
     * @return created spot entry
     */
    Spot create(Spot spot);

    void deleteById(Long id);
}
