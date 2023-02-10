package antifraud.persistence.repository;

import antifraud.domain.model.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {

    boolean existsByNumber(String number);

    Optional<StolenCard> findByNumber(String cardNumber);
}
