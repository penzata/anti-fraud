package antifraud.domain.model;

import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.model.enums.WorldRegion;

import java.time.LocalDateTime;

public class TransactionFactory {

    private TransactionFactory() {
    }

    public static Transaction create(Long depositMoney,
                                     String ipAddress,
                                     String cardNumber,
                                     WorldRegion region,
                                     LocalDateTime date) {
        return Transaction.builder()
                .money(depositMoney)
                .ipAddress(ipAddress)
                .cardNumber(cardNumber)
                .worldRegion(region)
                .dateTime(date)
                .build();
    }

    public static Transaction createWithFeedback(Long id,
                                                 TransactionResult feedback) {
        return Transaction.builder()
                .id(id)
                .feedback(feedback)
                .build();
    }
}