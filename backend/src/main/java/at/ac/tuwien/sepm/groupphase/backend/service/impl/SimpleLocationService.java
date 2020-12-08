package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.validator.LocationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SimpleLocationService implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationRepository locationRepository;
    private final LocationValidator validator;

    public SimpleLocationService(LocationRepository locationRepository, LocationValidator validator) {
        this.locationRepository = locationRepository;
        this.validator = validator;
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Override
    public Location create(Location location) throws ValidationException {
        LOGGER.debug("Create new location {}", location);
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
        LOGGER.debug("Searching for locations within a distance of at most " + radius + " km, containing spots with category: " + categoryId);


        List<Location> locations = locationRepository.filter(categoryId);

        if (locations.isEmpty()) {
            throw new ServiceException("No Location with these parameters found.");
        } else {
            try {
                return validator.validateLocationDistance(latitude, longitude, radius, locations);
            } catch (ValidationException e) {
                throw new ServiceException(e.getMessage());
            }
        }

    }

}
