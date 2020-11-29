package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundInDatabaseException;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.SpotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class SimpleSpotService implements SpotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SpotRepository spotRepository;

    public SimpleSpotService(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    @Override
    public Spot create(Spot spot) {
        LOGGER.debug("Create new Spot {}", spot);
        return spotRepository.save(spot);
    }

    @Override
    public void deleteById(Long id) throws NotFoundInDatabaseException {
        LOGGER.debug("Delete Spot with id {}", id);
        try {
            spotRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new NotFoundInDatabaseException("Spot does not exist");
        }
    }
}
