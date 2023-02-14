package antifraud.rest.dto;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.enums.UserAccess;
import lombok.Builder;

@Builder
public record UserStatusDTO(String username,
                            UserAccess access) {

    public static UserStatusDTO fromModel(CustomUser customUser) {
        return UserStatusDTO.builder()
                .username(customUser.getUsername())
                .access(customUser.getAccess())
                .build();
    }
}