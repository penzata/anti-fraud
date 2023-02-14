package antifraud.rest.controller;

import antifraud.domain.model.CustomUser;
import antifraud.domain.service.CustomUserService;
import antifraud.exceptions.ExistingUsernameException;
import antifraud.rest.dto.DeletedUserDTO;
import antifraud.rest.dto.UserAccessDTO;
import antifraud.rest.dto.UserDTO;
import antifraud.rest.dto.UserRoleDTO;
import antifraud.rest.dto.UserStatusDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin
public class UserController {
    private final CustomUserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user")
    public UserDTO createUser(@Valid @RequestBody UserDTO userDTO) {
        CustomUser registeredUser = userService.registerUser(userDTO.toModel())
                .orElseThrow(() -> new ExistingUsernameException(HttpStatus.CONFLICT));
        return UserDTO.fromModel(registeredUser);
    }

    @PreAuthorize("hasAnyRole('SUPPORT', 'ADMINISTRATOR')")
    @GetMapping("/list")
    public List<UserDTO> getUsers() {
        List<CustomUser> allUsers = userService.getUsers();
        return allUsers.stream()
                .map(UserDTO::fromModel)
                .toList();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/user/{username}")
    public DeletedUserDTO deleteUser(@PathVariable String username) {
        String realUsername = userService.retrieveRealUsername(username);
        userService.deleteUser(username);
        String returnMessage = "Deleted successfully!";
        return DeletedUserDTO.builder()
                .username(realUsername)
                .status(returnMessage)
                .build();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/role")
    UserDTO changeUserRole(@Valid @RequestBody UserRoleDTO userRoleDTO) {
        CustomUser changedUserRole = userService.changeUserRole(userRoleDTO.toModel());
        return UserDTO.fromModel(changedUserRole);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/access")
    UserAccessDTO grantAccess(@Valid @RequestBody UserAccessDTO userAccessDTO) {
        CustomUser userPermission = userService.grantAccess(userAccessDTO.toModel());
        return UserAccessDTO.fromModel(userPermission);
    }

    @PostMapping("/login")
    Map<String, String> login(@RequestBody String username) {

        return userService.login(username.replaceAll("\"", ""));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/list-access")
    List<UserStatusDTO> listUsersAccess() {
        List<CustomUser> usersPermissions = userService.getUsersPermissions();
        return usersPermissions.stream()
                .map(UserStatusDTO::fromModel)
                .toList();
    }
}