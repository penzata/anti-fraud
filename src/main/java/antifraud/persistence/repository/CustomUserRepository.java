package antifraud.persistence.repository;

import antifraud.domain.model.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {

    boolean existsByUsername(String username);

    Optional<CustomUser> findByUsernameIgnoreCase(String username);
}