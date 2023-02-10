package antifraud.domain.model;

import antifraud.domain.model.enums.UserAccess;
import antifraud.domain.model.enums.UserRole;

public class CustomUserFactory {

    private CustomUserFactory() {
    }

    public static CustomUser create(String name, String username, String password) {
        return CustomUser.builder()
                .name(name)
                .username(username)
                .password(password)
                .build();
    }

    public static CustomUser createWithRole(String username, UserRole role) {
        return CustomUser.builder()
                .username(username)
                .role(role)
                .build();
    }

    public static CustomUser createWithAccess(String username, UserAccess access) {
        return CustomUser.builder()
                .username(username)
                .access(access)
                .build();
    }
}