package antifraud.rest.dto;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import antifraud.domain.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;

@Builder
public record UserDTO(@JsonProperty(access = JsonProperty.Access.READ_ONLY)
                      Long id,
                      @NotBlank
                      String name,
                      @NotBlank
                      String username,
                      @NotBlank
                      @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                      String password,
                      @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                      UserRole role) {
    public static UserDTO fromModel(CustomUser registeredUser) {
        return UserDTO.builder()
                .id(registeredUser.getId())
                .name(registeredUser.getName())
                .username(registeredUser.getUsername())
                .role(registeredUser.getRole())
                .build();
    }

    public CustomUser toModel() {
        return CustomUserFactory.create(name, username, password);
    }
}