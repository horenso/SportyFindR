package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.RoleEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RoleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RoleMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.repository.RoleRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.RoleService;
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

import java.util.*;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RoleEndpointTest implements TestData {
    @Autowired
    private RoleEndpoint roleEndpoint;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleMapper roleMapper;

    @AfterEach
    public void afterEach() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    // positive tests

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void createRole() {
        RoleDto roleDto = RoleDto.builder()
            .name("TestRole")
            .build();
        RoleDto createdRole =  roleEndpoint.create(roleDto);
        Role foundRole = roleRepository.findRoleById(createdRole.getId()).get();
        assertAll(
            () -> assertEquals(roleDto.getName().toUpperCase(Locale.ROOT), createdRole.getName()),
            () -> assertEquals(roleDto.getName().toUpperCase(Locale.ROOT), foundRole.getName()),
            () -> assertEquals(createdRole.getId(), foundRole.getId()),
            () -> assertEquals(roleDto.getUserIds(), this.roleMapper.applicationUsersToUserIds(foundRole.getApplicationUsers()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void findAllRoles() {
        RoleDto roleDto = RoleDto.builder()
            .name("TestRole")
            .build();
        RoleDto createdRole =  roleEndpoint.create(roleDto);
        Role foundRole = roleRepository.findRoleById(createdRole.getId()).get();
        List<Role> foundRoles = roleService.findAll();
        assertAll(
            () -> assertThat(foundRoles, hasItems(foundRole))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void createRoleWithUserIdsNull() {
        RoleDto roleDto = RoleDto.builder()
            .name("TestRole")
            .userIds(null)
            .build();
        RoleDto createdRole =  roleEndpoint.create(roleDto);
        Role foundRole = roleRepository.findRoleById(createdRole.getId()).get();
        assertAll(
            () -> assertEquals(roleDto.getName().toUpperCase(Locale.ROOT), createdRole.getName()),
            () -> assertEquals(roleDto.getName().toUpperCase(Locale.ROOT), foundRole.getName()),
            () -> assertEquals(createdRole.getId(), foundRole.getId()),
            () -> assertEquals(roleDto.getUserIds(), this.roleMapper.applicationUsersToUserIds(foundRole.getApplicationUsers()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void createRoleWithUsers() {
        ApplicationUser newUser = ApplicationUser.builder()
            .name("name")
            .password("1234567")
            .email("hello@world.net")
            .enabled(true)
            .build();
        ApplicationUser savedUser = userRepository.save(newUser);
        List<Long> userList = new ArrayList<>();
        userList.add(savedUser.getId());

        RoleDto roleDto = RoleDto.builder()
            .name("TestRole")
            .userIds(userList)
            .build();
        RoleDto createdRole =  roleEndpoint.create(roleDto);
        Role foundRole = roleRepository.findRoleById(createdRole.getId()).get();

        ApplicationUser user = userRepository.findApplicationUserById(savedUser.getId()).get();
        assertAll(
            () -> assertEquals(roleDto.getName().toUpperCase(Locale.ROOT), createdRole.getName()),
            () -> assertEquals(roleDto.getName().toUpperCase(Locale.ROOT), foundRole.getName()),
            () -> assertEquals(createdRole.getId(), foundRole.getId()),
            () -> assertEquals(roleDto.getUserIds(), this.roleMapper.applicationUsersToUserIds(foundRole.getApplicationUsers()))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void deleteRole() {
        RoleDto roleDto = RoleDto.builder()
            .name("TestRole")
            .build();
        RoleDto createdRole =  roleEndpoint.create(roleDto);
        Role foundRole = roleRepository.findRoleById(createdRole.getId()).get();

        roleEndpoint.deleteRoleById(createdRole.getId());
        Optional<Role> deletedRole = roleRepository.findRoleById(createdRole.getId());
        assertAll(
            () -> assertFalse(deletedRole.isPresent())
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void findRoleByName() {
        RoleDto roleDto1 = RoleDto.builder()
            .name("TestRole")
            .build();
        RoleDto createdRole1 = roleEndpoint.create(roleDto1);

        assertAll(
            () -> assertTrue(this.roleService.roleExistsByName("TESTROLE")),
            () -> assertTrue(this.roleService.roleExistsByName("TestRole")),
            () -> assertFalse(this.roleService.roleExistsByName("noTestRole")),
            () -> assertEquals(this.roleService.findRoleByName("testRole").getId(), createdRole1.getId())
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void deleteRoleWithUsers() {
        ApplicationUser newUser = ApplicationUser.builder()
            .name("name")
            .password("1234567")
            .email("hello@world.net")
            .enabled(true)
            .build();
        ApplicationUser savedUser = userRepository.save(newUser);
        List<Long> userList = new ArrayList<>();
        userList.add(savedUser.getId());

        RoleDto roleDto1 = RoleDto.builder()
            .name("TestRole1")
            .userIds(userList)
            .build();
        RoleDto createdRole1 =  roleEndpoint.create(roleDto1);
        Role foundRole1 = roleRepository.findRoleById(createdRole1.getId()).get();

        RoleDto roleDto2 = RoleDto.builder()
            .name("TestRole2")
            .userIds(userList)
            .build();
        RoleDto createdRole2 =  roleEndpoint.create(roleDto2);
        Role foundRole2 = roleRepository.findRoleById(createdRole2.getId()).get();

        ApplicationUser user1 = userRepository.findApplicationUserById(savedUser.getId()).get();

        assertAll(
            () -> assertThat(user1.getRoles(), hasItems(foundRole1)),
            () -> assertThat(user1.getRoles(), hasItems(foundRole2)),
            () -> assertThat(user1.getRoles(), hasSize(2))
        );

        roleEndpoint.deleteRoleById(createdRole1.getId());
        ApplicationUser user2 = userRepository.findApplicationUserById(savedUser.getId()).get();

        assertAll(
            () -> assertThat(user2.getRoles(), not(hasItems(foundRole1))),
            () -> assertThat(user2.getRoles(), hasItems(foundRole2)),
            () -> assertThat(user2.getRoles(), hasSize(1))
        );

        roleEndpoint.deleteRoleById(createdRole2.getId());
        ApplicationUser user3 = userRepository.findApplicationUserById(savedUser.getId()).get();

        assertAll(
            () -> assertThat(user3.getRoles(), not(hasItems(foundRole1))),
            () -> assertThat(user3.getRoles(), not(hasItems(foundRole2))),
            () -> assertThat(user3.getRoles(), hasSize(0))
        );
    }


    // negative tests

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void createRoleWithTooShortName() {
        RoleDto roleDto = RoleDto.builder()
            .name("Rl") // na too short
            .build();

        assertAll(
            () -> assertThrows(ResponseStatusException.class, () -> roleEndpoint.create(roleDto))
        );
    }

    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, roles = "ADMIN")
    public void deleteInexistingRole() {
        assertAll(
            () -> assertThrows(ResponseStatusException.class, () -> roleEndpoint.deleteRoleById(1L))
        );
    }
}
