package antifraud.domain.service;

import antifraud.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Transaction processTransaction(Transaction transaction);

    Transaction giveFeedback(Transaction feedback);

    List<Transaction> showTransactionHistory();

    List<Transaction> showTransactionHistoryForSpecificCardNumber(String cardNumber);
}