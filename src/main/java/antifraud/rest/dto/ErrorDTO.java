package antifraud.rest.dto;

public record ErrorDTO(String httpStatus,
                       String message) {
}