package antifraud.rest.dto;

import antifraud.domain.model.IP;
import antifraud.domain.model.IPFactory;
import antifraud.validation.IpAddress;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;

@Builder
public record IpDTO(@JsonProperty(access = JsonProperty.Access.READ_ONLY)
                    Long id,
                    @NotBlank
                    @IpAddress
                    String ip) {

    public static IpDTO fromModel(IP savedIP) {
        return IpDTO.builder()
                .id(savedIP.getId())
                .ip(savedIP.getIpAddress())
                .build();
    }

    public IP toModel() {
        return IPFactory.create(ip);
    }
}