package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.Filter.LocationFilter;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface LocationService {

    /**
     * Get one location by id
     *
     * @param locationId of the location
     * @return the location entity
     * @throws NotFoundException if the location was not found
     */
    Location getOneById(Long locationId) throws NotFoundException;

    /**
     * Create a new location
     *
     * @param location to create
     * @return created location entry
     * @throws ValidationException if the location is not valid
     */
    Location create(Location location) throws ValidationException;

    /**
     * Finds locations containing spots that match the filter criteria
     *
     * @param locationFilter containing search parameters
     * @return List of locations containing spots that match the filter criteria
     */
    List<Location> find(LocationFilter locationFilter);
}
