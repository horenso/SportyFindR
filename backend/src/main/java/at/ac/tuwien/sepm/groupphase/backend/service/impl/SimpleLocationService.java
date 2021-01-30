package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationSearchObject;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.validator.LocationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleLocationService implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationValidator validator;

    @Override
    public Location getOneById(Long locationId) throws NotFoundException2 {
        Optional<Location> locationOptional = locationRepository.getOneById(locationId);
        if (locationOptional.isEmpty()) {
            throw new NotFoundException2("Location with ID " + locationId + " cannot be found!");
        }
        return locationOptional.get();
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
    public List<Location> find(LocationSearchObject locationSearchObject) throws ServiceException {
        log.debug("Searching for locations within a distance of at most " + locationSearchObject.getRadius() + " km, containing spots with category: " + locationSearchObject.getCategoryId());
        List<Location> locations;
        if (locationSearchObject.getCategoryId() != null && locationSearchObject.getCategoryId() != 0) {    // if search parameters contain category data
            locations = locationRepository.filter(locationSearchObject.getCategoryId());
        } else {
            locations = locationRepository.findAll();   // find all locations
        }
        try {
            if (locationSearchObject.getRadius() != null && locationSearchObject.getRadius() != 0) {      // if search parameters contain radius data
                return validator.validateLocationDistance(locationSearchObject.getLatitude(), locationSearchObject.getLongitude(), locationSearchObject.getRadius(), locations);
            } else {
                return locations;       // search by category only or no filter at all
            }
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }
    }

}
