package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.MessageEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.ReactionEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.SpotEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.UserEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SimpleUserMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest implements TestData {
    @Autowired
    private UserEndpoint userEndpoint;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SimpleUserMapper simpleUserMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SpotEndpoint spotEndpoint;
    @Autowired
    private MessageEndpoint messageEndpoint;
    @Autowired
    private ReactionEndpoint reactionEndpoint;
    @Autowired
    private SpotRepository spotRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReactionRepository reactionRepository;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private MockMvc mockMvc;


    @AfterEach
    public void afterEach() {
        reactionRepository.deleteAll();
        messageRepository.deleteAll();
        spotRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    // positive tests
    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void createUser() {
        NewUserDto userDto = NewUserDto.builder()
            .name("TestUser")
            .password("1234567")
            .email("hallo@welt.net")
            .enabled(true)
            .build();
        UserDto createdUser = userEndpoint.create(userDto);
        ApplicationUser foundUser = userRepository.findApplicationUserById(createdUser.getId()).get();
        assertAll(
            () -> assertEquals(userDto.getName(), createdUser.getName()),
            () -> assertEquals(userDto.getEmail(), createdUser.getEmail()),
            () -> assertEquals(userDto.getEnabled(), createdUser.getEnabled()),
            () -> assertEquals(userDto.getName(), foundUser.getName()),
            () -> assertEquals(userDto.getEmail(), foundUser.getEmail()),
            () -> assertEquals(userDto.getEnabled(), foundUser.getEnabled()),
            () -> assertEquals(createdUser.getId(), foundUser.getId()),
            () -> assertEquals(createdUser.getRoleIds(), this.userMapper.rolesToRoleIds(foundUser.getRoles()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void createUserWithRoleIdsNull() {
        NewUserDto userDto = NewUserDto.builder()
            .name("TestUser")
            .password("1234567")
            .email("hallo@welt.net")
            .roleId(null)
            .enabled(true)
            .build();
        UserDto createdUser = userEndpoint.create(userDto);
        ApplicationUser foundUser = userRepository.findApplicationUserById(createdUser.getId()).get();
        assertAll(
            () -> assertEquals(userDto.getName(), createdUser.getName()),
            () -> assertEquals(userDto.getEmail(), createdUser.getEmail()),
            () -> assertEquals(userDto.getEnabled(), createdUser.getEnabled()),
            () -> assertEquals(userDto.getName(), foundUser.getName()),
            () -> assertEquals(userDto.getEmail(), foundUser.getEmail()),
            () -> assertEquals(userDto.getEnabled(), foundUser.getEnabled()),
            () -> assertEquals(createdUser.getId(), foundUser.getId()),
            () -> assertEquals(createdUser.getRoleIds(), this.userMapper.rolesToRoleIds(foundUser.getRoles()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void createUserWithRoles() {
        Role newRole = Role.builder()
            .name("TESTROLE")
            .build();
        Role savedRole = roleRepository.save(newRole);
        List<Long> roleList = new ArrayList<>();
        roleList.add(savedRole.getId());
        NewUserDto userDto = NewUserDto.builder()
            .name("TestUser")
            .password("1234567")
            .email("hallo@welt.net")
            .enabled(true)
            .roleIds(roleList)
            .build();
        UserDto createdUser = userEndpoint.create(userDto);
        ApplicationUser foundUser = userRepository.findApplicationUserById(createdUser.getId()).get();
        assertAll(
            () -> assertEquals(userDto.getName(), createdUser.getName()),
            () -> assertEquals(userDto.getEmail(), createdUser.getEmail()),
            () -> assertEquals(userDto.getEnabled(), createdUser.getEnabled()),
            () -> assertEquals(userDto.getName(), foundUser.getName()),
            () -> assertEquals(userDto.getEmail(), foundUser.getEmail()),
            () -> assertEquals(userDto.getEnabled(), foundUser.getEnabled()),
            () -> assertEquals(createdUser.getId(), foundUser.getId()),
            () -> assertEquals(createdUser.getRoleIds(), this.userMapper.rolesToRoleIds(foundUser.getRoles()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void deleteUser() {
        NewUserDto userDto = NewUserDto.builder()
            .name("TestUser")
            .password("1234567")
            .email("hallo@welt.net")
            .enabled(true)
            .build();
        UserDto createdUser = userEndpoint.create(userDto);
        ApplicationUser foundUser = userRepository.findApplicationUserById(createdUser.getId()).get();
        userEndpoint.deleteUserById(createdUser.getId());
        Optional<ApplicationUser> deletedUser = userRepository.findApplicationUserById(createdUser.getId());
        assertAll(
            () -> assertFalse(deletedUser.isPresent())
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void deleteUserWithRole() {
        Role newRole = Role.builder()
            .name("TESTROLE")
            .build();
        Role savedRole = roleRepository.save(newRole);
        List<Long> roleList = new ArrayList<>();
        roleList.add(savedRole.getId());
        NewUserDto userDto = NewUserDto.builder()
            .name("TestUser")
            .password("1234567")
            .email("hallo@welt.net")
            .enabled(true)
            .roleIds(roleList)
            .build();
        UserDto createdUser = userEndpoint.create(userDto);
        ApplicationUser foundUser = userRepository.findApplicationUserById(createdUser.getId()).get();
        userEndpoint.deleteUserById(createdUser.getId());
        Optional<ApplicationUser> deletedUser = userRepository.findApplicationUserById(createdUser.getId());
        Optional<Role> persistentRole = roleRepository.findRoleById(savedRole.getId());
        assertAll(
            () -> assertFalse(deletedUser.isPresent()),
            () -> assertTrue(persistentRole.isPresent())
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void getOneById() {
        ApplicationUser user = ApplicationUser.builder()
            .name("TestUser")
            .password("1234567")
            .email("hallo@welt.net")
            .enabled(true)
            .build();
        ApplicationUser createdUser = userRepository.save(user);
        UserDto foundUser = userEndpoint.getOneById(createdUser.getId());
        assertAll(
            () -> assertEquals(user.getName(), foundUser.getName()),
            () -> assertEquals(user.getEmail(), foundUser.getEmail()),
            () -> assertEquals(user.getEnabled(), foundUser.getEnabled()),
            () -> assertEquals(createdUser.getId(), foundUser.getId())
            // Don't check for roles - this is not exactly necessary
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void getOneByEmail() {
        ApplicationUser user = ApplicationUser.builder()
            .name("TestUser")
            .password("1234567")
            .email("hallo@welt.net")
            .enabled(true)
            .build();
        ApplicationUser createdUser = userRepository.save(user);
        UserDto foundUser = userEndpoint.getOneByEmail(createdUser.getEmail());
        assertAll(
            () -> assertEquals(user.getName(), foundUser.getName()),
            () -> assertEquals(user.getEmail(), foundUser.getEmail()),
            () -> assertEquals(user.getEnabled(), foundUser.getEnabled()),
            () -> assertEquals(createdUser.getId(), foundUser.getId())
            // Don't check for roles - this is not exactly necessary
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void updateUser() {
        NewUserDto userDto = NewUserDto.builder()
            .name("TestUser")
            .password("1234567")
            .email("hallo@welt.net")
            .enabled(true)
            .build();
        UserDto createdUser = userEndpoint.create(userDto);
        ApplicationUser foundUser = userRepository.findApplicationUserById(createdUser.getId()).get();
        assertAll(
            () -> assertEquals(userDto.getName(), createdUser.getName()),
            () -> assertEquals(userDto.getEmail(), createdUser.getEmail()),
            () -> assertEquals(userDto.getEnabled(), createdUser.getEnabled()),
            () -> assertEquals(userDto.getName(), foundUser.getName()),
            () -> assertEquals(userDto.getEmail(), foundUser.getEmail()),
            () -> assertEquals(userDto.getEnabled(), foundUser.getEnabled()),
            () -> assertEquals(createdUser.getId(), foundUser.getId()),
            () -> assertEquals(createdUser.getRoleIds(), this.userMapper.rolesToRoleIds(foundUser.getRoles()))
        );
        createdUser.setEmail("hallo@neuewelt.net");
        createdUser.setName("Neuer TestUser");
        UserDto changedUser = userEndpoint.update(createdUser);
        ApplicationUser changedFoundUser = userRepository.findApplicationUserById(createdUser.getId()).get();
        assertAll(
            () -> assertEquals(createdUser.getName(), changedUser.getName()),
            () -> assertEquals(createdUser.getEmail(), changedUser.getEmail()),
            () -> assertEquals(createdUser.getEnabled(), changedUser.getEnabled()),
            () -> assertEquals(createdUser.getName(), changedFoundUser.getName()),
            () -> assertEquals(createdUser.getEmail(), changedFoundUser.getEmail()),
            () -> assertEquals(createdUser.getEnabled(), changedFoundUser.getEnabled()),
            () -> assertEquals(createdUser.getId(), changedFoundUser.getId()),
            () -> assertEquals(createdUser.getRoleIds(), this.userMapper.rolesToRoleIds(changedFoundUser.getRoles()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void getAll() {
        NewUserDto userDto1 = NewUserDto.builder()
            .name("TestUser1")
            .password("1234567")
            .email("hallo@welt1.net")
            .enabled(true)
            .build();
        UserDto createdUser1 = userEndpoint.create(userDto1);
        NewUserDto userDto2 = NewUserDto.builder()
            .name("TestUser2")
            .password("1234567")
            .email("hallo@welt2.net")
            .enabled(true)
            .build();
        UserDto createdUser2 = userEndpoint.create(userDto2);
        List<UserDto> foundUsers = userEndpoint.getAll();
        assertAll(
            () -> assertThat(foundUsers, hasItems(createdUser1)),
            () -> assertThat(foundUsers, hasItems(createdUser2)),
            () -> assertThat(foundUsers, hasSize(2))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void findUsersByRole() {
        Role newRole = Role.builder()
            .name("TESTROLE")
            .build();
        Role savedRole = roleRepository.save(newRole);
        List<Long> roleList = new ArrayList<>();
        roleList.add(savedRole.getId());
        NewUserDto userDto1 = NewUserDto.builder()
            .name("TestUser1")
            .password("1234567")
            .email("hallo@welt1.net")
            .enabled(true)
            .roleIds(roleList)
            .build();
        UserDto createdUser1 = userEndpoint.create(userDto1);
        NewUserDto userDto2 = NewUserDto.builder()
            .name("TestUser2")
            .password("1234567")
            .email("hallo@welt2.net")
            .enabled(true)
            .roleIds(roleList)
            .build();
        UserDto createdUser2 = userEndpoint.create(userDto2);
        List<UserDto> foundUsers = userEndpoint.getUsersByRole(savedRole.getId());
        assertAll(
            () -> assertThat(foundUsers, hasItems(createdUser1)),
            () -> assertThat(foundUsers, hasItems(createdUser2)),
            () -> assertThat(foundUsers, hasSize(2))
        );
    }

    // negative tests
    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void createUserWithoutPassword() {
        NewUserDto userDto = NewUserDto.builder()
            .name("TestUser")
            .email("hallo@welt.net")
            .enabled(true)
            .build();

        assertAll(
            () -> assertThrows(ResponseStatusException.class, () -> userEndpoint.create(userDto))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void updateUserWithAlreadyTakenEmail() {
        NewUserDto userDto1 = NewUserDto.builder()
            .name("TestUser1")
            .email("hallo@welt1.net")
            .password("1234567")
            .enabled(true)
            .build();
        UserDto createdUser1 = userEndpoint.create(userDto1);
        NewUserDto userDto2 = NewUserDto.builder()
            .name("TestUser2")
            .email("hallo@welt2.net")
            .password("1234567")
            .enabled(true)
            .build();
        UserDto createdUser2 = userEndpoint.create(userDto2);

        createdUser1.setEmail(createdUser2.getEmail());

        assertAll(
            () -> assertThrows(ResponseStatusException.class, () -> userEndpoint.update(createdUser1))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void deleteInexistingUser() {
        assertAll(
            () -> assertThrows(ResponseStatusException.class, () -> userEndpoint.deleteUserById(1L))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void findByInexistingRole() {
        assertAll(
            () -> assertThrows(ResponseStatusException.class, () -> userEndpoint.getUsersByRole(1L))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void findByInexistingId() {
        assertAll(
            () -> assertThrows(ResponseStatusException.class, () -> userEndpoint.getOneById(1L))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void findByInexistingEmail() {
        assertAll(
            () -> assertThrows(ResponseStatusException.class, () -> userEndpoint.getOneByEmail("not@existing.com"))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void cascadingDeleteUser() {
        NewUserDto userDto = NewUserDto.builder()
            .name(USERNAME)
            .password(PASSWORD)
            .email(EMAIL)
            .enabled(ENABLED)
            .build();
        UserDto createdUser = userEndpoint.create(userDto);

        NewUserDto pUserDto = NewUserDto.builder()
            .name("PersistentUser")
            .password("1234567")
            .email("persistent@welt.net")
            .enabled(ENABLED)
            .build();
        UserDto pCreatedUser = userEndpoint.create(pUserDto);

        ApplicationUser foundUser = userRepository.findApplicationUserById(createdUser.getId()).get();
        ApplicationUser pFoundUser = userRepository.findApplicationUserById(pCreatedUser.getId()).get();

        Set<ApplicationUser> userSet = new HashSet<>();
        userSet.add(foundUser);
        Role newRole = Role.builder()
            .name("TESTROLE")
            .applicationUsers(userSet)
            .build();
        Role savedRole = roleRepository.save(newRole);

        CategoryDto categoryDto = CategoryDto.builder()
            .name(CAT_NAME)
            .build();
        LocationDto locationDto = LocationDto.builder()
            .latitude(LAT)
            .longitude(LONG)
            .build();
        SpotDto initSpotDto = SpotDto.builder()
            .owner(this.simpleUserMapper.userToSimpleUserDto(foundUser))
            .name(NAME)
            .location(locationDto)
            .build();
        initSpotDto.setCategory(categoryMapper.categoryToCategoryDto(categoryRepository.save(categoryMapper.categoryDtoToCategory(categoryDto))));
        SpotDto spotDto = spotEndpoint.create(initSpotDto);
        Spot spot = spotRepository.getOneById(spotDto.getId()).get();

        Message message = Message.builder()
            .owner(foundUser)
            .spot(spot)
            .content(MESSAGE_CONTENT)
            .publishedAt(LocalDateTime.now())
            .build();
        Message message1 = messageRepository.save(message);

        Message pMessage = Message.builder()
            .owner(pFoundUser)
            .spot(spot)
            .content(MESSAGE_CONTENT)
            .publishedAt(LocalDateTime.now())
            .build();
        Message pMessage1 = messageRepository.save(pMessage);

        ReactionDto reactionDto = ReactionDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(foundUser))
            .messageId(message.getId())
            .type(ReactionDto.ReactionDtoType.THUMBS_DOWN)
            .build();
        reactionDto.setId(reactionEndpoint.create(reactionDto).getId());

        ReactionDto reactionDto2 = ReactionDto.builder()
            .owner(simpleUserMapper.userToSimpleUserDto(pFoundUser))
            .messageId(pMessage.getId())
            .type(ReactionDto.ReactionDtoType.THUMBS_DOWN)
            .build();
        reactionDto2.setId(reactionEndpoint.create(reactionDto2).getId());


        ApplicationUser assertFoundUser = userRepository.findApplicationUserByName(createdUser.getName()).get();
        Role assertFoundRole = roleRepository.findRoleById(savedRole.getId()).get();
        Spot assertFoundSpot = spotRepository.getOneById(spotDto.getId()).get();
        Message assertFoundMessage = messageRepository.findById(message1.getId()).get();
        Message assertFoundPMessage = messageRepository.findById(pMessage1.getId()).get();
        Reaction assertFoundReaction = reactionRepository.findById(reactionDto.getId()).get();
        Reaction assertFoundPReaction = reactionRepository.findById(reactionDto2.getId()).get();

        assertAll(
            () -> assertEquals(createdUser.getId(), assertFoundUser.getId()),
            () -> assertThat(assertFoundUser.getRoles(), hasItems(savedRole)),
            () -> assertThat(assertFoundRole.getApplicationUsers(), hasItems(foundUser)),
            () -> assertEquals(assertFoundSpot.getOwner(), foundUser),
            () -> assertEquals(assertFoundMessage.getOwner(), foundUser),
            () -> assertEquals(assertFoundPMessage.getOwner(), pFoundUser),
            () -> assertEquals(assertFoundReaction.getOwner(), foundUser),
            () -> assertEquals(assertFoundPReaction.getOwner(), foundUser)
        );

        userEndpoint.deleteUserById(createdUser.getId());

        assertAll(
            () -> assertFalse(userRepository.findApplicationUserByName(createdUser.getName()).isPresent()),
            () -> assertThat(roleRepository.findRoleById(savedRole.getId()).get().getApplicationUsers(), not(hasItems(foundUser))),
            () -> assertTrue(spotRepository.getOneById(spotDto.getId()).isPresent()),
            () -> assertNull(spotRepository.getOneById(spotDto.getId()).get().getOwner()),
            () -> assertFalse(messageRepository.findById(message1.getId()).isPresent()),
            () -> assertEquals(messageRepository.findById(pMessage1.getId()).get().getOwner(), pFoundUser),
            () -> assertFalse(reactionRepository.findById(reactionDto.getId()).isPresent()),
            () -> assertFalse(reactionRepository.findById(reactionDto2.getId()).isPresent())
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void filterUsersByName() throws Exception {
        ApplicationUser user = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name("owner")
            .password(PASSWORD)
            .build();
        ApplicationUser user2 = ApplicationUser.builder()
            .email(EMAIL)
            .enabled(ENABLED)
            .name("loner")
            .password(PASSWORD)
            .build();

        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(
            get("/api/v1/users/filter?name=owner")
                .header(securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertTrue(response.getContentAsString().contains("\"id\":"+user.getId()+",\"name\":\"owner\""))
        );
    }


}
