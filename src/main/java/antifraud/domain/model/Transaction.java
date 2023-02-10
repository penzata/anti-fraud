package antifraud.domain.model;

import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.model.enums.WorldRegion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "card_date_idx", columnList = "cardNumber, dateTime"),
        @Index(name = "feedback_idx", columnList = "feedback")
})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.PRIVATE)
    private Long id;
    private Long money;
    private String ipAddress;
    private String cardNumber;
    @Enumerated(EnumType.STRING)
    private TransactionResult transactionResult;
    @Enumerated(EnumType.STRING)
    private WorldRegion worldRegion;
    private LocalDateTime dateTime;
    private String transactionInfo;
    @Enumerated(EnumType.STRING)
    private TransactionResult feedback;
}