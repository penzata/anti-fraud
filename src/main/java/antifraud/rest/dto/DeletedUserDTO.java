package antifraud.rest.dto;

import lombok.Builder;

@Builder
public record DeletedUserDTO(String username,
                             String status) {
}