package antifraud.domain.service.impl;

import antifraud.domain.model.IP;
import antifraud.domain.service.SuspiciousIPService;
import antifraud.exceptions.IpNotFoundException;
import antifraud.persistence.repository.SuspiciousIPRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SuspiciousIPServiceImpl implements SuspiciousIPService {
    private final SuspiciousIPRepository suspiciousIPRepository;

    @Transactional
    @Override
    public Optional<IP> saveSuspiciousAddress(IP address) {
        return suspiciousIPRepository.existsByIpAddress(address.getIpAddress()) ?
                Optional.empty() :
                Optional.of(suspiciousIPRepository.save(address));
    }

    @Transactional
    @Override
    public void removeIpAddress(String ipAddress) {
        IP foundIpAddress = suspiciousIPRepository.findByIpAddress(ipAddress)
                .orElseThrow(() -> new IpNotFoundException(ipAddress));
        suspiciousIPRepository.deleteById(foundIpAddress.getId());
    }

    @Override
    public List<IP> showIpAddresses() {
        return suspiciousIPRepository.findAll().stream()
                .sorted(Comparator.comparingLong(IP::getId))
                .toList();
    }

    @Override
    public boolean existsByIpAddress(String ipAddress) {

        return suspiciousIPRepository.existsByIpAddress(ipAddress);
    }
}