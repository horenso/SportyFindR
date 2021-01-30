package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.UserEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.repository.RoleRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;

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

    @AfterEach
    public void afterEach() {
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
        UserDto createdUser =  userEndpoint.create(userDto);
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
        UserDto createdUser =  userEndpoint.create(userDto);
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
        UserDto createdUser =  userEndpoint.create(userDto);
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
        UserDto createdUser =  userEndpoint.create(userDto);
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
        UserDto createdUser =  userEndpoint.create(userDto);
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
        ApplicationUser createdUser =  userRepository.save(user);
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
        ApplicationUser createdUser =  userRepository.save(user);
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
        UserDto createdUser =  userEndpoint.create(userDto);
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
        UserDto changedUser =  userEndpoint.update(createdUser);
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
        UserDto createdUser1 =  userEndpoint.create(userDto1);
        NewUserDto userDto2 = NewUserDto.builder()
            .name("TestUser2")
            .password("1234567")
            .email("hallo@welt2.net")
            .enabled(true)
            .build();
        UserDto createdUser2 =  userEndpoint.create(userDto2);
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
        UserDto createdUser1 =  userEndpoint.create(userDto1);
        NewUserDto userDto2 = NewUserDto.builder()
            .name("TestUser2")
            .password("1234567")
            .email("hallo@welt2.net")
            .enabled(true)
            .roleIds(roleList)
            .build();
        UserDto createdUser2 =  userEndpoint.create(userDto2);
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
}
