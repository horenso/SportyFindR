package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SimpleLocationService implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationRepository locationRepository;

    public SimpleLocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Override
    public Location create(Location location) throws ServiceException {
        LOGGER.debug("Create new location {}", location);
        if (location.getLatitude() == null) {
            throw new ServiceException("Latitude must not be Null");
        }
        if (location.getLongitude() == null) {
            throw new ServiceException("Longitude must not be Null");
        }
        if (location.getLatitude() < -90) {
            throw new ServiceException("Latitude can not be below -90");
        }
        if (location.getLatitude() > 90) {
            throw new ServiceException("Latitude can not be above 90");
        }
        if (location.getLongitude() < -180) {
            throw new ServiceException("Longitude can not be below -180");
        }
        if (location.getLongitude() > 180) {
            throw new ServiceException("Longitude can not be above 180");
        }
        return locationRepository.save(location);
    }

    @Override
    public List<Location> filter(Long categoryId) throws ServiceException {
        LOGGER.debug("Searching for locations containing spots with: " +
            "category: " + categoryId);

        List<Location> locations = locationRepository.filter(categoryId);

        if (locations.isEmpty()) {
            throw new ServiceException("No Location with these parameters found.");
        } else {
            return locationRepository.filter(categoryId);
        }

    }

}
