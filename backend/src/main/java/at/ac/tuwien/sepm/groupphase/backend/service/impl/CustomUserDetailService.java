package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Role;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException2;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.RoleRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public ApplicationUser createApplicationUser(ApplicationUser user) throws ValidationException {
        if (false) {
            // ToDo check user validity
            throw new ValidationException("User invalid");
        }
        ApplicationUser apUser = new ApplicationUser(null, user.getName(), user.getEmail(), this.passwordEncoder.encode(user.getPassword()), user.getEnabled(), user.getRoles());
        return this.userRepository.save(apUser);
    }

    @Override
    public void deleteApplicationUserById(Long id) throws NotFoundException2 {
        Optional<ApplicationUser> user = userRepository.findApplicationUserById(id);
        if (user.isPresent()) {
            this.userRepository.delete(user.get());
        } else {
            throw new NotFoundException2("User not found.");
        }
    }

    @Override
    public List<ApplicationUser> findAll() {
        LOGGER.debug("Find all Users");
        return this.userRepository.findAll();
    }

    @Override
    public ApplicationUser update(ApplicationUser user) throws NotFoundException2 {
        Optional<ApplicationUser> rUser = userRepository.findApplicationUserById(user.getId());
        if (rUser.isPresent()) {
            this.userRepository.save(user);
            return findApplicationUserById(user.getId());
        } else {
            throw new NotFoundException2("User cannot be updated as user does not exist.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList();
            List<Role> roles = roleRepository.findRolesByApplicationUsersId(applicationUser.getId());
            for (Role role : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException2 e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) throws NotFoundException2 {
        LOGGER.debug("Find application user by email");
        Optional<ApplicationUser> oApplicationUser = userRepository.findApplicationUserByEmail(email);
        if (oApplicationUser.isPresent()) {
            return oApplicationUser.get();
        } else {
            throw new NotFoundException2(String.format("Could not find the user with the email address %s", email));
        }
    }

    @Override
    public ApplicationUser findApplicationUserById(Long id) throws NotFoundException2 {
        LOGGER.debug("Find application user by id");
        Optional<ApplicationUser> optionalApplicationUser = userRepository.findApplicationUserById(id);
        if (optionalApplicationUser.isPresent()) {
            return optionalApplicationUser.get();
        } else {
            throw new NotFoundException2(String.format("Could not find the user with the id %s", id));
        }
    }

    public List<ApplicationUser> findApplicationUserByRoleId(Long roleId) throws NotFoundException2 {
        LOGGER.debug("Find application users by role id");
        if (roleRepository.findRoleById(roleId).isPresent()) {
            return userRepository.findApplicationUsersByRolesId(roleId);
        } else {
            throw new NotFoundException2(String.format("Could not find the role with the id %s", roleId));
        }
    }


}
