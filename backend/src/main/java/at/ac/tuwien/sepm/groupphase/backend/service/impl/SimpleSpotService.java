package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class SimpleSpotService implements SpotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SpotRepository spotRepository;
    private final LocationService locationService;

    public SimpleSpotService(SpotRepository spotRepository, LocationService locationService) {
        this.spotRepository = spotRepository;
        this.locationService = locationService;
    }

    @Override
    public Spot create(Spot spot) {
        LOGGER.debug("Create new Spot {}", spot);
        Location location = spot.getLocation();
        if (location.getId()==null && location.getLatitude()!=0.0 && location.getLongitude()!=0.0) {
            locationService.create(spot.getLocation());
        }
        return spotRepository.save(spot);
    }


}
