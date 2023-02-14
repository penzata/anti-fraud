package antifraud.domain.service.impl;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import antifraud.domain.model.UserPrincipal;
import antifraud.domain.model.enums.UserAccess;
import antifraud.domain.model.enums.UserRole;
import antifraud.domain.service.CustomUserService;
import antifraud.exceptions.AccessViolationException;
import antifraud.exceptions.AlreadyProvidedException;
import antifraud.exceptions.ExistingAdministratorException;
import antifraud.persistence.repository.CustomUserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CustomUserServiceImpl implements CustomUserService {
    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public Optional<CustomUser> registerUser(CustomUser userCredentials) {
        userCredentials.setPassword(encoder.encode(userCredentials.getPassword()));
        authorize(userCredentials);
        return customUserRepository.existsByUsername(userCredentials.getUsername()) ?
                Optional.empty() :
                Optional.of(customUserRepository.save(userCredentials));
    }

    private void authorize(CustomUser userCredentials) {
        if (customUserRepository.count() == 0) {
            userCredentials.setRole(UserRole.ADMINISTRATOR);
            userCredentials.setAccess(UserAccess.UNLOCK);
        } else {
            userCredentials.setRole(UserRole.MERCHANT);
            userCredentials.setAccess(UserAccess.LOCK);
        }
    }

    @Override
    public List<CustomUser> getUsers() {
        return customUserRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String username) {
        CustomUser foundUser = foundByUsername(username);
        customUserRepository.deleteById(foundUser.getId());
    }

    private CustomUser foundByUsername(String username) {
        return customUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional
    @Override
    public CustomUser changeUserRole(CustomUser userWithRole) {
        CustomUser foundUser = foundByUsername(userWithRole.getUsername());
        roleCheckForCollision(userWithRole, foundUser);
        if (UserRole.ADMINISTRATOR.equals(foundUser.getRole())) {
            throw new ExistingAdministratorException();
        }
        foundUser.setRole(userWithRole.getRole());
        return customUserRepository.save(foundUser);
    }

    /**
     * Checks if the provided to be changed role is the same as the current role of the user.
     * If it is, method throws an exception.
     *
     * @param providedRole User with the Role to be changed.
     * @param currentRole  User with current user's Role.
     */
    private void roleCheckForCollision(CustomUser providedRole, CustomUser currentRole) {
        if (providedRole.getRole().equals(currentRole.getRole())) {
            throw new AlreadyProvidedException();
        }
    }

    @Transactional
    @Override
    public CustomUser grantAccess(CustomUser userWithAccessLevel) {
        CustomUser foundUser = foundByUsername(userWithAccessLevel.getUsername());
        roleCheckForAdmin(foundUser);
        foundUser.setAccess(userWithAccessLevel.getAccess());
        return customUserRepository.save(foundUser);
    }

    /**
     * Checks if the role of the user's access to be changed is Administrator.
     * If it is, the method throws exception, because Administrator access cannot be blocked
     * or manipulated at all.
     *
     * @param currentRole User with current role.
     */
    private void roleCheckForAdmin(CustomUser currentRole) {
        if (UserRole.ADMINISTRATOR.equals(currentRole.getRole())) {
            throw new AccessViolationException();
        }
    }

    /**
     * @param username URI template variable.
     * @return real case-sensitive username based on the URI template variable.
     */
    @Override
    public String retrieveRealUsername(String username) {
        CustomUser foundUser = foundByUsername(username);
        return foundUser.getUsername();
    }

    @Override
    public Map<String, String> login(String username) {
        CustomUser foundUser = foundByUsername(username);
        return Map.of("username", foundUser.getUsername(),
                "role", foundUser.getRole().name());
    }

    @Override
    public List<CustomUser> getUsersPermissions() {
        List<CustomUser> users = customUserRepository.findAll();
        return users.stream()
                .filter(u -> !UserRole.ADMINISTRATOR.equals(u.getRole()))
                .map(user -> CustomUserFactory
                        .createWithAccess(user.getUsername(), user.getAccess()))
                .toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrincipal(foundByUsername(username));
    }
}