package antifraud.domain.service;

import antifraud.domain.model.CustomUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomUserService extends UserDetailsService {

    Optional<CustomUser> registerUser(CustomUser userCredentials);

    List<CustomUser> getUsers();

    void deleteUser(String username);

    CustomUser changeUserRole(CustomUser userWithRole);

    CustomUser grantAccess(CustomUser userWithAccessLevel);

    String retrieveRealUsername(String username);

    Map<String, String> login(String username);

    List<CustomUser> getUsersPermissions();
}