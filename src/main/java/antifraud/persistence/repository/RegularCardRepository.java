package antifraud.persistence.repository;

import antifraud.domain.model.RegularCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularCardRepository extends JpaRepository<RegularCard, Long> {
    boolean existsByNumber(String cardNumber);

    RegularCard findByNumber(String cardNumber);
}