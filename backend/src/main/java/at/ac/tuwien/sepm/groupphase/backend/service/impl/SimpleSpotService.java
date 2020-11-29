package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
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
    public Spot create(Spot spot) throws ServiceException {
        LOGGER.debug("Create new Spot {}", spot);
        Location location = spot.getLocation();
        if (spot.getName()==null){
            throw new ServiceException("Name must not be Empty");
        }
        try {
            return spotRepository.save(spot);
        }catch (Exception e){
            throw new ServiceException("The Spot must have a valid Location");
        }
    }


}
