package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface LocationService {

    /**
     * Get all existing locations from database.
     * @throws RuntimeException  if something goes wrong during data processing.
     * @throws NotFoundException if no categories could be found in the system.
     */
    List<Location> findAll();

    /**
     * Create a new location
     *
     * @param location to create
     * @return created location entry
     */
    Location create(Location location) throws ServiceException, ValidationException;

    /**
     * Finds locations containing spots that match the filter criteria
     *
     * @param categoryId of spot contained in location
     * @return List of locations containing spots that match the filter criteria
     */
    List<Location> filter(Long categoryId, Double latitude, Double longitude, Double radius) throws NotFoundException, ServiceException;
}
