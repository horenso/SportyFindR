package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.validator.LocationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleLocationService implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationRepository locationRepository;
    private final LocationValidator validator;

    @Override
    public List<Location> findAll() {

        LOGGER.debug("Get all categories.");
        try {
            return locationRepository.findAll();
        } catch (NotFoundException e) {
            LOGGER.error("No locations found.");
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public Location create(Location location) throws ValidationException {
        log.debug("Create new location {}", location);
        if (location.getLatitude() == null) {
            throw new ValidationException("Latitude must not be Null");
        }
        if (location.getLongitude() == null) {
            throw new ValidationException("Longitude must not be Null");
        }
        return locationRepository.save(location);
    }

    @Override
    public List<Location> filter(Long categoryId, Double latitude, Double longitude, Double radius) throws ServiceException {
        log.debug("Searching for locations within a distance of at most " + radius + " km, containing spots with category: " + categoryId);


        List<Location> locations = locationRepository.filter(categoryId);

        if (locations.isEmpty()) {
            throw new ServiceException("No Location with these parameters found.");
        } else {
            try {
                if (radius != 0) {      // if search parameters contain radius data
                    return validator.validateLocationDistance(latitude, longitude, radius, locations);
                } else {
                    return locations;       // search by category only
                }
            } catch (ValidationException e) {
                throw new ServiceException(e.getMessage());
            }
        }

    }

}
