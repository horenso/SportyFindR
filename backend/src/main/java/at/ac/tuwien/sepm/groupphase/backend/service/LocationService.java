package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;

public interface LocationService {

    /**
     * Create a new location
     *
     * @param location to create
     * @return created location entry
     */
    Location create(Location location);
}
