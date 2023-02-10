package antifraud.rest.dto;

import lombok.Builder;

@Builder
public record ViolationDTO(String fieldName,
                           String message) {
}