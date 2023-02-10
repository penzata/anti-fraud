package antifraud.persistence.repository;

import antifraud.domain.model.IP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspiciousIPRepository extends JpaRepository<IP, Long> {

    boolean existsByIpAddress(String ipAddress);

    Optional<IP> findByIpAddress(String ipAddress);
}