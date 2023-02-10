package antifraud.rest.dto;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import antifraud.domain.model.enums.UserAccess;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Builder
public record UserAccessDTO(@NotBlank
                            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                            String username,
                            @NotNull
                            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                            UserAccess operation,
                            @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                            String status) {
    public static UserAccessDTO fromModel(CustomUser userPermission) {
        String customMessage = String.format("User %s %sED!",
                userPermission.getUsername(), userPermission.getAccess());

        return UserAccessDTO.builder()
                .status(customMessage)
                .build();
    }

    public CustomUser toModel() {
        return CustomUserFactory.createWithAccess(username, operation);
    }
}