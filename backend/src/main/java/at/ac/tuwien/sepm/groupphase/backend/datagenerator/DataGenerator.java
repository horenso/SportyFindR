package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Spot;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Profile("generateData")
@Component
public class DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // Parameters
    private static final int NUMBER_OF_LOCATIONS = 20;
    private static final int NUMBER_OF_SPOTS = NUMBER_OF_LOCATIONS * 5;
    private static final int NUMBER_OF_MESSAGES = 200;
    private static final String SPOT_NAME = "Test Spot #";
    private static final String SPOT_DESCRIPTION = "Description of Test Spot #";
    private static final String MESSAGE_CONTENT = "This is the text of the message";

    // Coordinates around vienna
    private static final Double MIN_LATITUDE = 48.05;
    private static final Double MAX_LATITUDE = 48.37;
    private static final Double MIN_LONGITUDE = 16.13;
    private static final Double MAX_LONGITUDE = 16.64;
    private static final Random RANDOM = new Random();

    private final SpotRepository spotRepository;
    private final LocationRepository locationRepository;
    private final MessageRepository messageRepository;

    public DataGenerator(
        SpotRepository spotRepository,
        LocationRepository locationRepository,
        MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.spotRepository = spotRepository;
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    private void generateData() {
        generateLocationsAndSpots();
        generateMessage();
    }

    private void generateMessage() {
        if (messageRepository.findAll().size() > 0) {
            LOGGER.debug("Already messages in the repository!");
            return;
        }

        List<Spot> spotList = spotRepository.findAll();

        LOGGER.debug("generating {} message entries", NUMBER_OF_MESSAGES);
        for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
            Message message = Message.builder()
                .content(MESSAGE_CONTENT + " " + i)
                .publishedAt(LocalDateTime.now().minusMonths(i))
                .spot(spotList.get(i % NUMBER_OF_SPOTS))
                .build();
            LOGGER.debug("saving message {}", message);
            messageRepository.save(message);
        }
    }

    private void generateLocationsAndSpots() {
        if (locationRepository.findAll().size() > 0 || spotRepository.findAll().size() > 0) {
            LOGGER.debug("Already spots or locations in the repositories.");
            return;
        }

        LOGGER.debug("generating {} location entries", NUMBER_OF_SPOTS);
        for (int i = 1; i <= NUMBER_OF_LOCATIONS; i++) {
            Location location = Location.builder()
                .latitude(MIN_LATITUDE + (RANDOM.nextDouble() * (MAX_LATITUDE - MIN_LATITUDE)))
                .longitude(MIN_LONGITUDE + (RANDOM.nextDouble() * (MAX_LONGITUDE - MIN_LONGITUDE)))
                .build();
            LOGGER.debug("saving location {}", location);
            locationRepository.save(location);
        }

        List<Location> locationList = locationRepository.findAll();

        LOGGER.debug("generating {} spot entries", NUMBER_OF_SPOTS);
        for (int i = 0; i < NUMBER_OF_SPOTS; i++) {
            Spot spot = Spot.builder()
                .name(SPOT_NAME + i)
                .description(SPOT_DESCRIPTION + i)
                .location(locationList.get(i % NUMBER_OF_LOCATIONS))
                .build();
            LOGGER.debug("saving spot {}", spot);
            spotRepository.save(spot);
        }
    }
}
