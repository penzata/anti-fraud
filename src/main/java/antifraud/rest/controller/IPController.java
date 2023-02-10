package antifraud.rest.controller;

import antifraud.domain.model.IP;
import antifraud.domain.service.SuspiciousIPService;
import antifraud.validation.IpAddress;
import antifraud.exceptions.ExistingIpException;
import antifraud.rest.dto.CustomMessageDTO;
import antifraud.rest.dto.IpDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@PreAuthorize("hasRole('SUPPORT')")
@RequestMapping("/api/antifraud/suspicious-ip")
public class IPController {
    private final SuspiciousIPService suspiciousIPService;

    @PostMapping()
    public IpDTO saveSuspiciousIp(@Valid @RequestBody IpDTO ipDTO) {
        IP savedIP = suspiciousIPService.saveSuspiciousAddress(ipDTO.toModel())
                .orElseThrow(() -> new ExistingIpException(HttpStatus.CONFLICT));
        return IpDTO.fromModel(savedIP);
    }

    @DeleteMapping("/{ip}")
    public CustomMessageDTO deleteAddress(@IpAddress @PathVariable String ip) {
        suspiciousIPService.removeIpAddress(ip);
        String returnMessage = String.format("IP %s successfully removed!", ip);
        return new CustomMessageDTO(returnMessage);
    }

    @GetMapping()
    public List<IpDTO> getIpAddresses() {
        List<IP> allIpAddresses = suspiciousIPService.showIpAddresses();
        return allIpAddresses.stream()
                .map(IpDTO::fromModel)
                .toList();
    }
}