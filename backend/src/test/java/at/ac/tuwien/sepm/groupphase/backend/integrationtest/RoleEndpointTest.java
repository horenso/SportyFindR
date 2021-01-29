package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.RoleEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RoleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RoleMapper;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RoleEndpointTest implements TestData {
    @Autowired
    private RoleEndpoint roleEndpoint;
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
