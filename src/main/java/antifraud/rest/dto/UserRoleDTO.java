package antifraud.rest.dto;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import antifraud.domain.model.enums.UserRole;
import antifraud.validation.AvailableRole;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;

@Builder
public record UserRoleDTO(@NotBlank
                          String username,
                          @NotBlank
                          @AvailableRole
                          String role) {

    public CustomUser toModel() {
        return CustomUserFactory.createWithRole(username,
                UserRole.valueOf(role));
    }
}