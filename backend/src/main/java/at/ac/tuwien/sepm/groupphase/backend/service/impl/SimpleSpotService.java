package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.lang.invoke.MethodHandles;

@Service
public class SimpleSpotService implements SpotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SpotRepository spotRepository;
    private final LocationRepository locationRepository;

    public SimpleSpotService(SpotRepository spotRepository, LocationRepository locationRepository) {
        this.spotRepository = spotRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public Spot create(Spot spot) {
        LOGGER.debug("Create new Spot {}", spot);
        return spotRepository.save(spot);
    }

    @Override
    public void deleteById(Long id) throws ValidationException {
        LOGGER.debug("Delete Spot with id {}", id);
        var spot = spotRepository.findById(id);
        if(spot.isEmpty()){
            throw new ValidationException("Spot does not exist");
        }
        spotRepository.deleteById(id);
        if (spotRepository.findLocationWithSpot(spot.get().getLocation().getId()).isEmpty()){
            locationRepository.deleteById(spot.get().getLocation().getId());
        }
    }
}
