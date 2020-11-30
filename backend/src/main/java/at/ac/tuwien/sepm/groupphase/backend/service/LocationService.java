package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;

import java.util.List;

public interface LocationService {

    /**
     * Find all locations
     *
     * @return list of all location entities
     */
    List<Location> findAll();

    /**
     * Create a new location
     *
     * @param location to create
     * @return created location entry
     */
    Location create(Location location);
}
