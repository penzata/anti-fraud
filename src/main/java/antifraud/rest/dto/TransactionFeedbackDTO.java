package antifraud.rest.dto;

import antifraud.domain.model.Transaction;
import antifraud.domain.model.TransactionFactory;
import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.model.enums.WorldRegion;
import antifraud.validation.AvailableFeedback;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
public record TransactionFeedbackDTO(@Min(1)
                                     @NotNull
                                     Long transactionId,
                                     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                                     Long amount,
                                     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                                     String ip,
                                     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                                     String number,
                                     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                                     WorldRegion region,
                                     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                                     LocalDateTime date,
                                     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                                     TransactionResult result,
                                     @NotBlank
                                     @AvailableFeedback
                                     String feedback) {
    public static TransactionFeedbackDTO fromModel(Transaction transactionWithFeedback) {
        String feedbackValue = transactionWithFeedback.getFeedback() == null ? "" :
                transactionWithFeedback.getFeedback().name();

        return TransactionFeedbackDTO.builder()
                .transactionId(transactionWithFeedback.getId())
                .amount(transactionWithFeedback.getMoney())
                .ip(transactionWithFeedback.getIpAddress())
                .number(transactionWithFeedback.getCardNumber())
                .region(transactionWithFeedback.getWorldRegion())
                .date(transactionWithFeedback.getDateTime())
                .result(transactionWithFeedback.getTransactionResult())
                .feedback(feedbackValue)
                .build();
    }

    public Transaction toModel() {
        return TransactionFactory.createWithFeedback(transactionId,
                TransactionResult.valueOf(feedback));
    }
}