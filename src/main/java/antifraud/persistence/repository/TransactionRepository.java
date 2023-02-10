package antifraud.persistence.repository;

import antifraud.domain.model.Transaction;
import antifraud.domain.model.enums.TransactionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCardNumberAndDateTimeBetween(String cardNumber,
                                                         LocalDateTime hourStart,
                                                         LocalDateTime hourEnd);

    boolean existsByFeedbackAndFeedbackNotNull(TransactionResult feedback);

    List<Transaction> findTransactionByCardNumber(String cardNumber);
}