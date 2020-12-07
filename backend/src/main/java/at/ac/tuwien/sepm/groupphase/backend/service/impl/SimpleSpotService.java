package at.ac.tuwien.sepm.groupphase.backend.service.impl;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundInDatabaseException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
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
    private final LocationService locationService;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    public SimpleSpotService(SpotRepository spotRepository, LocationService locationService, LocationRepository locationRepository, CategoryRepository categoryRepository) {
        this.spotRepository = spotRepository;
        this.locationService = locationService;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Spot create(Spot spot) throws ValidationException,ServiceException {
        LOGGER.debug("Create new Spot {}", spot);
        if(spot.getId()!=null){
            throw new ValidationException("Id must be null");
        }
        if(spot.getCategory().getId()==null){
            throw new ValidationException("Spot must have a Category");
        }
        if(categoryRepository.findById(spot.getCategory().getId()).isEmpty()){
            throw new ValidationException("Category does not Exist");
        }
        if(spot.getLocation().getId()==null){
            try {
                spot.setLocation(locationService.create(spot.getLocation()));
            }catch (ValidationException e){
                throw new ServiceException(e.getMessage());
            }
        }
        if(locationRepository.findById(spot.getLocation().getId()).isEmpty()){
            throw new ValidationException("Location does not Exist");
        }
        return spotRepository.save(spot);
    }
//ToDo: Fix notFoundinDatabaseException
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
