package antifraud.rest.controller;

import antifraud.domain.model.Transaction;
import antifraud.domain.service.TransactionService;
import antifraud.rest.dto.TransactionDTO;
import antifraud.rest.dto.TransactionFeedbackDTO;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/antifraud")
public class TransactionController {
    private final TransactionService transactionService;

    @PreAuthorize("hasRole('MERCHANT')")
    @PostMapping("/transaction")
    public TransactionDTO makeTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction deposit = transactionService.processTransaction(transactionDTO.toModel());
        return TransactionDTO.fromModel(deposit);
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @PutMapping("/transaction")
    public TransactionFeedbackDTO addFeedback(@Valid @RequestBody TransactionFeedbackDTO transactionFeedbackDTO) {
        Transaction transactionWithFeedback = transactionService.giveFeedback(transactionFeedbackDTO.toModel());
        return TransactionFeedbackDTO.fromModel(transactionWithFeedback);
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/history")
    public List<TransactionFeedbackDTO> getHistory() {
        List<Transaction> transactionHistory = transactionService.showTransactionHistory();
        return transactionHistory.stream()
                .map(TransactionFeedbackDTO::fromModel)
                .toList();
    }

    @PreAuthorize("hasRole('SUPPORT')")
    @GetMapping("/history/{number}")
    public List<TransactionFeedbackDTO> getHistoryForCardNumber(@NotBlank
                                                                @CreditCardNumber
                                                                @PathVariable String number) {
        List<Transaction> transactionsForCard = transactionService.showTransactionHistoryForSpecificCardNumber(number);
        return transactionsForCard.stream()
                .map(TransactionFeedbackDTO::fromModel)
                .toList();
    }
}