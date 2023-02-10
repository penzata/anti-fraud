package antifraud.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomMessageDTO(@JsonProperty("status")
                               String message) {
}