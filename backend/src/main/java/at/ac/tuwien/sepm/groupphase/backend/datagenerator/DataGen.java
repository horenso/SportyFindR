package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ServiceException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.*;
import com.github.javafaker.App;
import lombok.extern.slf4j.Slf4j;
import com.github.javafaker.Faker;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Profile("datagen")
@Component
public class DataGen {

    // Parameters
    private static final String ADMIN_ROLE_NAME = "ADMIN";
    private static final String USER_ROLE_NAME = "USER";
    private static final int NUMBER_OF_USERS = 5;
    private static final int NUMBER_OF_LOCATIONS = 186;
    private static final int NUMBER_OF_SPOTS = NUMBER_OF_LOCATIONS * 2;
    private static final int NUMBER_OF_MESSAGES = NUMBER_OF_SPOTS * 2;

    private static final Random random = new Random();
    private final static String COMMA_DELIMITER = ",";
    private final Faker faker;
    private final PasswordEncoder passwordEncoder;


    private static final HashMap<String, String[]> CREW = createCrew();
    private static final HashMap<String, String> CATEGORIES = createMap();

    private final UserService userService;
    private final RoleService roleService;
    private final CategoryService categoryService;
    private final LocationRepository locationRepository;
    private final SpotRepository spotRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final HashtagService hashtagService;

    public DataGen(UserService userService,
                   RoleService roleService,
                   CategoryService categoryService,
                   PasswordEncoder passwordEncoder,
                   LocationRepository locationRepository,
                   SpotRepository spotRepository,
                   CategoryRepository categoryRepository,
                   UserRepository userRepository,
                   MessageRepository messageRepository,
                   HashtagService hashtagService
                   ) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
        this.categoryService = categoryService;
        this.locationRepository = locationRepository;
        this.spotRepository = spotRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.hashtagService = hashtagService;
        faker = new Faker(new Locale("es"));
    }

    @PostConstruct
    private void generateData() throws NotFoundException, ValidationException, IOException, ServiceException {
        generateAdminUserLogin();
        generateCategories();
        generateLocations();
        generateSpots();
        generateMessages();
    }

    private static HashMap<String, String []> createCrew() {
        HashMap<String, String[]> crew = new HashMap<>();
        crew.put("Jannis Adamek", new String[] {"jannisAdamek", "jannis@sportyfindr.com"});
        crew.put("Marcel Jira", new String[] {"marcelJira", "marcel@sportyfindr.com"});
        crew.put("Victoria Leskoschek", new String[] {"viciLeskoschek", "vici@sportyfindr.com"});
        crew.put("Simon Linder", new String[] {"simonLinder", "simon@sportyfindr.com"});
        crew.put("Fisnik Miftari", new String[] {"fisnikMiftari", "fisnik@sportyfindr.com"});
        crew.put("Florian Mold", new String[] {"florianMold", "florian@sportyfindr.com"});
        return crew;
    }

    private static HashMap<String, String> createMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Baseball", "sports_baseball");
        map.put("Basketball", "sports_basketball");
        map.put("Cricket", "sports_cricket");
        map.put("Football", "sports_football");
        map.put("Golf", "sports_golf");
        map.put("Handball", "sports_handball");
        map.put("Hiking", "hiking");
        map.put("Hockey", "sports_hockey");
        map.put("Ice Skating", "ice_skating");
        map.put("Kabaddi", "sports_kabaddi");
        map.put("Kitesurfing", "kitesurfing");
        map.put("Meditation", "self_improvement");
        map.put("Motorsports", "sports_motorsports");
        map.put("Nordic Walking", "nordic_walking");
        map.put("Rugby", "sports_rugby");
        map.put("Soccer", "sports_soccer");
        map.put("Skateboarding", "skateboarding");
        map.put("Skiing", "downhill_skiing");
        map.put("Tennis", "sports_tennis");
        map.put("Volleyball", "sports_volleyball");
        return map;
    }


    // 1. ROLES
    // 1.1 ADMIN
    @PostConstruct
    private void generateAdminUserLogin() throws ValidationException, NotFoundException {
        try {
            Role adminRole = this.generateRole(ADMIN_ROLE_NAME);
            Role userRole = this.generateRole(USER_ROLE_NAME);

            HashSet<Role> roles = new HashSet<>();
            roles.add(userRole);
            this.generateUsers(roles); // only user role
            roles.add(adminRole);
            this.generateAdmins(roles); // user + admin role
        } catch (ValidationException e) {
            throw new ValidationException(e);
        } catch (NotFoundException e) {
            throw new NotFoundException(e);
        }
    }

    // 1.2 USER
    private Role generateRole(String roleName) throws ValidationException, NotFoundException {
        if (!roleService.roleExistsByName(roleName)) {
            try {
                Role role = Role.builder()
                    .name(roleName)
                    .build();
                return roleService.create(role);
            } catch (ValidationException e) {
                throw new ValidationException("Couldn't create User Role", e);
            }
        } else {
            log.info("Role " + roleName + " was already created");
            try {
                return roleService.findRoleByName(roleName);
            } catch (NotFoundException e) {
                throw new NotFoundException(roleName + " role not found", e);
            }
        }
    }

    // 2. USER
    // 2.1 ADMIN USERS
    private void generateAdmins(HashSet<Role> roles) throws ValidationException, NotFoundException {
        for (Map.Entry<String, String[]> mapElement : CREW.entrySet()) {
            String key = mapElement.getKey();
            String[] value = mapElement.getValue();
            if (!userService.userExistsByEmail(value[1])) {
                try {
                    ApplicationUser user = ApplicationUser.builder()
                        .name(key)
                        .email(value[1])
                        .password(value[0])
                        .enabled(true)
                        .roles(roles)
                        .build();
                    userService.createApplicationUser(user);
                } catch (ValidationException e) {
                    throw new ValidationException("Couldn't create Admin User", e);
                }
            } else {
                log.info("Admin User was already created, updating admin user.");
                try {
                    ApplicationUser user = userService.getApplicationUserByEmail(value[1]);
                    user.setName(key);
                    user.setEmail(value[1]);
                    user.setPassword(value[0]);
                    user.setEnabled(true);
                    user.setRoles(roles);
                    userService.update(user);
                } catch (NotFoundException e) {
                    throw new NotFoundException("Couldn't find Admin User", e);
                }
            }
        }
    }

    // 2.2 SIMPLE USER
    private void generateUsers(HashSet<Role> roles) throws ValidationException, NotFoundException {
        Set<String> emails = new HashSet<>();

        for (int i = 1; i <= NUMBER_OF_USERS; i++) {
            String currEmail = faker.internet().emailAddress();
            while (emails.contains(currEmail)) {
                currEmail = faker.internet().emailAddress();
            }
            emails.add(currEmail);
            try {
                ApplicationUser user = ApplicationUser.builder()
                    .name(faker.name().firstName() + " " + faker.name().lastName())
                    .email(currEmail)
                    .password(passwordEncoder.encode("password"))
                    .enabled(true)
                    .roles(roles)
                    .build();
                userService.createApplicationUser(user);
            } catch (ValidationException e) {
                throw new ValidationException("Couldn't create User", e);
            }
        }
    }

    // 3. CATEGORIES
    private void generateCategories() throws ValidationException {

        if (categoryRepository.findAll().size() > 0) {
            log.debug("Already categories in the repository.");
            return;
        }

        try {
            for (Map.Entry<String, String> mapElement : CATEGORIES.entrySet()) {
                String key = mapElement.getKey();
                String value = mapElement.getValue();
                Category cat = Category.builder()
                    .name(key)
                    .icon(value)
                    .build();
                categoryService.create(cat);
            }
        } catch (ValidationException e) {
            throw new ValidationException("Couldn't create Category", e);
        }
    }

    // 4. LOCATIONS
    private void generateLocations() throws IOException {

        if (locationRepository.findAll().size() > 0) {
            log.debug("Already locations in the repository.");
            return;
        }

        log.info("generating {} location entries", NUMBER_OF_LOCATIONS);

        //Generator
        try (BufferedReader br = new BufferedReader(new FileReader("locations-datagen/locations.csv"))) {

            List<List<String>> result = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                result.add(Arrays.asList(values));
            }


            for (int i = 0; i <= NUMBER_OF_LOCATIONS-1; i++) {
                Location location = Location.builder()
                    .latitude(Double.parseDouble(result.get(i).get(0)))
                    .longitude(Double.parseDouble(result.get(i).get(1)))
                    .build();
                System.out.print("Generating location.. " + i + "\r");
                locationRepository.save(location);
            }
        }

        //Vienna
        try (BufferedReader br = new BufferedReader(new FileReader("locations-datagen/locations-vienna.csv"))) {

            List<List<String>> resultVie = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                resultVie.add(Arrays.asList(values));
            }


            for (int i = 0; i < 5; i++) {
                Location location = Location.builder()
                    .latitude(Double.parseDouble(resultVie.get(i).get(0)))
                    .longitude(Double.parseDouble(resultVie.get(i).get(1)))
                    .build();
                System.out.print("Generating location.. " + i + "\r");
                locationRepository.save(location);
            }
        }


    }

    // 5. SPOTS
    private void generateSpots() throws ValidationException {

        if (spotRepository.findAll().size() > 0) {
            log.debug("Already spots in the repository.");
            return;
        }

        List<Location> locationList = locationRepository.findAll();
        List<Category> categoryList = categoryRepository.findAll();
        List<ApplicationUser> userList = userRepository.findAll();

            log.debug("generating {} spot entries", NUMBER_OF_SPOTS);
            for (int i = 0; i < (NUMBER_OF_SPOTS/2); i++) {

                String description = faker.harryPotter().location();
                while (description.length() > 20) {
                    description = faker.harryPotter().location();
                }

                int id = random.nextInt(20);
                Spot spot = Spot.builder()
                    .name(description)
                    .description(faker.harryPotter().spell())
                    .location(locationList.get(i % NUMBER_OF_LOCATIONS))
                    .owner(userList.get(i % (NUMBER_OF_USERS + 6)))
                    .category(categoryList.get(id))
                    .build();
                System.out.print("Generating spots.. " + i + "\r");
                spotRepository.save(spot);
            }

        for (int i = (NUMBER_OF_SPOTS/2); i < NUMBER_OF_SPOTS; i++) {

            String name = faker.harryPotter().location();
            while (name.length() > 20) {
                name = faker.harryPotter().location();
            }
            String description = ("#" + faker.harryPotter().spell()).trim();
            int id = random.nextInt(20);
            Spot spot = Spot.builder()
                .name(name)
                .description(description)
                .location(locationList.get(i % NUMBER_OF_LOCATIONS))
                .owner(userList.get(i & (NUMBER_OF_USERS + 6)))
                .category(categoryList.get(id))
                .build();
            System.out.print("Generating spots.. " + i + "\r");
            spotRepository.save(spot);
            hashtagService.acquireHashtags(spot);
        }


    }

    // 6. MESSAGES
    private void generateMessages() throws ValidationException {
        if (messageRepository.findAll().size() > 0) {
            log.info("Already messages in the repository.");
            return;
        }

        List<Spot> spotList = spotRepository.findAll();
        List<ApplicationUser> userList = userRepository.findAll();

            log.info("generating {} message entries", NUMBER_OF_MESSAGES);

            for (int i = 0; i < (NUMBER_OF_MESSAGES / 2); i++) {
                int hoursVariance = 48 + random.nextInt(24 * 7 * 4 * 12);
                int down = random.nextInt(5);
                int up = random.nextInt(5);
                LocalDateTime dateTime = LocalDateTime.now().minusHours(hoursVariance);
                Message message = Message.builder()
                    .publishedAt(dateTime)
                    .content(faker.yoda().quote())
                    .downVotes(down)
                    .upVotes(up)
                    .owner(userList.get(i % (NUMBER_OF_USERS + 6)))
                    .spot(spotList.get(i % NUMBER_OF_SPOTS))
                    .build();
                System.out.print("Generating messages.. " + i + "\r");
                messageRepository.save(message);
            }

            for (int i = (NUMBER_OF_MESSAGES / 2); i < NUMBER_OF_MESSAGES - 1; i++) {
                int hoursVariance = 48 + random.nextInt(24 * 7 * 4 * 12);
                int down = random.nextInt(5);
                int up = random.nextInt(5);
                LocalDateTime dateTime = LocalDateTime.now().minusHours(hoursVariance);
                String hashtag = ("#" + faker.superhero().power()).trim();
                Message message = Message.builder()
                    .publishedAt(dateTime)
                    .content(hashtag)
                    .downVotes(down)
                    .upVotes(up)
                    .owner(userList.get(i % (NUMBER_OF_USERS + 6)))
                    .spot(spotList.get(i % NUMBER_OF_SPOTS))
                    .build();
                System.out.print("Generating messages.. " + i + "\r");
                messageRepository.save(message);
                hashtagService.acquireHashtags(message);
            }


    }
}
