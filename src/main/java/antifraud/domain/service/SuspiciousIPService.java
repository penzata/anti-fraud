package antifraud.domain.service;


import antifraud.domain.model.IP;

import java.util.List;
import java.util.Optional;

public interface SuspiciousIPService {
    Optional<IP> saveSuspiciousAddress(IP address);

    void removeIpAddress(String ipAddress);

    List<IP> showIpAddresses();

    boolean existsByIpAddress(String ipAddress);

}